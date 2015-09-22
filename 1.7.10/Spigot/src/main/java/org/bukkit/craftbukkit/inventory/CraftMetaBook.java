package org.bukkit.craftbukkit.inventory;

// Spigot start
import static org.spigotmc.ValidateUtils.limit;
// Spigot end

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta;
import org.bukkit.inventory.meta.BookMeta;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap.Builder;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaBook extends CraftMetaItem implements BookMeta {
	static final ItemMetaKey BOOK_TITLE = new ItemMetaKey("title");
	static final ItemMetaKey BOOK_AUTHOR = new ItemMetaKey("author");
	static final ItemMetaKey BOOK_PAGES = new ItemMetaKey("pages");
	static final int MAX_PAGE_LENGTH = 256;
	static final int MAX_TITLE_LENGTH = 0xffff;

	private String title;
	private String author;
	private List<String> pages = new ArrayList<String>();

	CraftMetaBook(CraftMetaItem meta) {
		super(meta);

		if (!(meta instanceof CraftMetaBook))
			return;
		CraftMetaBook bookMeta = (CraftMetaBook) meta;
		title = bookMeta.title;
		author = bookMeta.author;
		pages.addAll(bookMeta.pages);
	}

	CraftMetaBook(NBTTagCompound tag) {
		super(tag);

		if (tag.hasKey(BOOK_TITLE.NBT)) {
			title = limit(tag.getString(BOOK_TITLE.NBT), 1024); // Spigot
		}

		if (tag.hasKey(BOOK_AUTHOR.NBT)) {
			author = limit(tag.getString(BOOK_AUTHOR.NBT), 1024); // Spigot
		}

		if (tag.hasKey(BOOK_PAGES.NBT)) {
			NBTTagList pages = tag.getList(BOOK_PAGES.NBT, 8);
			String[] pageArray = new String[pages.size()];

			for (int i = 0; i < pages.size(); i++) {
				String page = limit(pages.getString(i), 2048); // Spigot
				pageArray[i] = page;
			}

			addPage(pageArray);
		}
	}

	CraftMetaBook(Map<String, Object> map) {
		super(map);

		setAuthor(SerializableMeta.getString(map, BOOK_AUTHOR.BUKKIT, true));

		setTitle(SerializableMeta.getString(map, BOOK_TITLE.BUKKIT, true));

		Iterable<?> pages = SerializableMeta.getObject(Iterable.class, map, BOOK_PAGES.BUKKIT, true);
		CraftMetaItem.safelyAdd(pages, this.pages, MAX_PAGE_LENGTH);
	}

	@Override
	void applyToItem(NBTTagCompound itemData) {
		super.applyToItem(itemData);

		if (hasTitle()) {
			itemData.setString(BOOK_TITLE.NBT, title);
		}

		if (hasAuthor()) {
			itemData.setString(BOOK_AUTHOR.NBT, author);
		}

		if (hasPages()) {
			itemData.set(BOOK_PAGES.NBT, createStringList(pages));
		}
	}

	@Override
	boolean isEmpty() {
		return super.isEmpty() && isBookEmpty();
	}

	boolean isBookEmpty() {
		return !(hasPages() || hasAuthor() || hasTitle());
	}

	@Override
	boolean applicableTo(Material type) {
		switch (type) {
		case WRITTEN_BOOK:
		case BOOK_AND_QUILL:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean hasAuthor() {
		return !Strings.isNullOrEmpty(author);
	}

	@Override
	public boolean hasTitle() {
		return !Strings.isNullOrEmpty(title);
	}

	@Override
	public boolean hasPages() {
		return !pages.isEmpty();
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean setTitle(final String title) {
		if (title == null) {
			this.title = null;
			return true;
		} else if (title.length() > MAX_TITLE_LENGTH)
			return false;

		this.title = title;
		return true;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public void setAuthor(final String author) {
		this.author = author;
	}

	@Override
	public String getPage(final int page) {
		Validate.isTrue(isValidPage(page), "Invalid page number");
		return pages.get(page - 1);
	}

	@Override
	public void setPage(final int page, final String text) {
		if (!isValidPage(page))
			throw new IllegalArgumentException("Invalid page number " + page + "/" + pages.size());

		pages.set(page - 1, text == null ? "" : text.length() > MAX_PAGE_LENGTH ? text.substring(0, MAX_PAGE_LENGTH) : text);
	}

	@Override
	public void setPages(final String... pages) {
		this.pages.clear();

		addPage(pages);
	}

	@Override
	public void addPage(final String... pages) {
		for (String page : pages) {
			if (page == null) {
				page = "";
			} else if (page.length() > MAX_PAGE_LENGTH) {
				page = page.substring(0, MAX_PAGE_LENGTH);
			}

			this.pages.add(page);
		}
	}

	@Override
	public int getPageCount() {
		return pages.size();
	}

	@Override
	public List<String> getPages() {
		return ImmutableList.copyOf(pages);
	}

	@Override
	public void setPages(List<String> pages) {
		this.pages.clear();
		CraftMetaItem.safelyAdd(pages, this.pages, MAX_PAGE_LENGTH);
	}

	private boolean isValidPage(int page) {
		return page > 0 && page <= pages.size();
	}

	@Override
	public CraftMetaBook clone() {
		CraftMetaBook meta = (CraftMetaBook) super.clone();
		meta.pages = new ArrayList<String>(pages);
		return meta;
	}

	@Override
	int applyHash() {
		final int original;
		int hash = original = super.applyHash();
		if (hasTitle()) {
			hash = 61 * hash + title.hashCode();
		}
		if (hasAuthor()) {
			hash = 61 * hash + 13 * author.hashCode();
		}
		if (hasPages()) {
			hash = 61 * hash + 17 * pages.hashCode();
		}
		return original != hash ? CraftMetaBook.class.hashCode() ^ hash : hash;
	}

	@Override
	boolean equalsCommon(CraftMetaItem meta) {
		if (!super.equalsCommon(meta))
			return false;
		if (meta instanceof CraftMetaBook) {
			CraftMetaBook that = (CraftMetaBook) meta;

			return (hasTitle() ? that.hasTitle() && title.equals(that.title) : !that.hasTitle()) && (hasAuthor() ? that.hasAuthor() && author.equals(that.author) : !that.hasAuthor()) && (hasPages() ? that.hasPages() && pages.equals(that.pages) : !that.hasPages());
		}
		return true;
	}

	@Override
	boolean notUncommon(CraftMetaItem meta) {
		return super.notUncommon(meta) && (meta instanceof CraftMetaBook || isBookEmpty());
	}

	@Override
	Builder<String, Object> serialize(Builder<String, Object> builder) {
		super.serialize(builder);

		if (hasTitle()) {
			builder.put(BOOK_TITLE.BUKKIT, title);
		}

		if (hasAuthor()) {
			builder.put(BOOK_AUTHOR.BUKKIT, author);
		}

		if (hasPages()) {
			builder.put(BOOK_PAGES.BUKKIT, pages);
		}

		return builder;
	}
}
