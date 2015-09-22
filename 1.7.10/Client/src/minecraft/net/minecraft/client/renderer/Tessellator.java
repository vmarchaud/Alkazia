package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.PriorityQueue;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.client.util.QuadComparator;
import org.lwjgl.opengl.GL11;

public class Tessellator
{
    /** The byte buffer used for GL allocation. */
    private ByteBuffer byteBuffer;

    /** The same memory as byteBuffer, but referenced as an integer buffer. */
    private IntBuffer intBuffer;

    /** The same memory as byteBuffer, but referenced as an float buffer. */
    private FloatBuffer floatBuffer;

    /** The same memory as byteBuffer, but referenced as an short buffer. */
    private ShortBuffer shortBuffer;

    /** Raw integer array. */
    private int[] rawBuffer;

    /**
     * The number of vertices to be drawn in the next draw call. Reset to 0 between draw calls.
     */
    private int vertexCount;

    /** The first coordinate to be used for the texture. */
    private double textureU;

    /** The second coordinate to be used for the texture. */
    private double textureV;
    private int brightness;

    /** The color (RGBA) value to be used for the following draw call. */
    private int color;

    /**
     * Whether the current draw object for this tessellator has color values.
     */
    private boolean hasColor;

    /**
     * Whether the current draw object for this tessellator has texture coordinates.
     */
    private boolean hasTexture;
    private boolean hasBrightness;

    /**
     * Whether the current draw object for this tessellator has normal values.
     */
    private boolean hasNormals;

    /** The index into the raw buffer to be used for the next data. */
    private int rawBufferIndex;

    /**
     * The number of vertices manually added to the given draw call. This differs from vertexCount because it adds extra
     * vertices when converting quads to triangles.
     */
    private int addedVertices;

    /** Disables all color information for the following draw call. */
    private boolean isColorDisabled;

    /** The draw mode currently being used by the tessellator. */
    private int drawMode;

    /**
     * An offset to be applied along the x-axis for all vertices in this draw call.
     */
    private double xOffset;

    /**
     * An offset to be applied along the y-axis for all vertices in this draw call.
     */
    private double yOffset;

    /**
     * An offset to be applied along the z-axis for all vertices in this draw call.
     */
    private double zOffset;

    /** The normal to be applied to the face being drawn. */
    private int normal;

    /** The static instance of the Tessellator. */
    public static final Tessellator instance = new Tessellator(2097152);

    /** Whether this tessellator is currently in draw mode. */
    private boolean isDrawing;

    /** The size of the buffers used (in integers). */
    private int bufferSize;
    private static final String __OBFID = "CL_00000960";

    private Tessellator(int p_i1250_1_)
    {
        this.bufferSize = p_i1250_1_;
        this.byteBuffer = GLAllocation.createDirectByteBuffer(p_i1250_1_ * 4);
        this.intBuffer = this.byteBuffer.asIntBuffer();
        this.floatBuffer = this.byteBuffer.asFloatBuffer();
        this.shortBuffer = this.byteBuffer.asShortBuffer();
        this.rawBuffer = new int[p_i1250_1_];
    }

    /**
     * Draws the data set up in this tessellator and resets the state to prepare for new drawing.
     */
    public int draw()
    {
        if (!this.isDrawing)
        {
            throw new IllegalStateException("Not tesselating!");
        }
        else
        {
            this.isDrawing = false;

            if (this.vertexCount > 0)
            {
                this.intBuffer.clear();
                this.intBuffer.put(this.rawBuffer, 0, this.rawBufferIndex);
                this.byteBuffer.position(0);
                this.byteBuffer.limit(this.rawBufferIndex * 4);

                if (this.hasTexture)
                {
                    this.floatBuffer.position(3);
                    GL11.glTexCoordPointer(2, 32, this.floatBuffer);
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (this.hasBrightness)
                {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                    this.shortBuffer.position(14);
                    GL11.glTexCoordPointer(2, 32, this.shortBuffer);
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                }

                if (this.hasColor)
                {
                    this.byteBuffer.position(20);
                    GL11.glColorPointer(4, true, 32, this.byteBuffer);
                    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (this.hasNormals)
                {
                    this.byteBuffer.position(24);
                    GL11.glNormalPointer(32, this.byteBuffer);
                    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                }

                this.floatBuffer.position(0);
                GL11.glVertexPointer(3, 32, this.floatBuffer);
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glDrawArrays(this.drawMode, 0, this.vertexCount);
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

                if (this.hasTexture)
                {
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (this.hasBrightness)
                {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                }

                if (this.hasColor)
                {
                    GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (this.hasNormals)
                {
                    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }

            int var1 = this.rawBufferIndex * 4;
            this.reset();
            return var1;
        }
    }

    public TesselatorVertexState getVertexState(float p_147564_1_, float p_147564_2_, float p_147564_3_)
    {
        int[] var4 = new int[this.rawBufferIndex];
        PriorityQueue var5 = new PriorityQueue(this.rawBufferIndex, new QuadComparator(this.rawBuffer, p_147564_1_ + (float)this.xOffset, p_147564_2_ + (float)this.yOffset, p_147564_3_ + (float)this.zOffset));
        byte var6 = 32;
        int var7;

        for (var7 = 0; var7 < this.rawBufferIndex; var7 += var6)
        {
            var5.add(Integer.valueOf(var7));
        }

        for (var7 = 0; !var5.isEmpty(); var7 += var6)
        {
            int var8 = ((Integer)var5.remove()).intValue();

            for (int var9 = 0; var9 < var6; ++var9)
            {
                var4[var7 + var9] = this.rawBuffer[var8 + var9];
            }
        }

        System.arraycopy(var4, 0, this.rawBuffer, 0, var4.length);
        return new TesselatorVertexState(var4, this.rawBufferIndex, this.vertexCount, this.hasTexture, this.hasBrightness, this.hasNormals, this.hasColor);
    }

    public void setVertexState(TesselatorVertexState p_147565_1_)
    {
        System.arraycopy(p_147565_1_.getRawBuffer(), 0, this.rawBuffer, 0, p_147565_1_.getRawBuffer().length);
        this.rawBufferIndex = p_147565_1_.getRawBufferIndex();
        this.vertexCount = p_147565_1_.getVertexCount();
        this.hasTexture = p_147565_1_.getHasTexture();
        this.hasBrightness = p_147565_1_.getHasBrightness();
        this.hasColor = p_147565_1_.getHasColor();
        this.hasNormals = p_147565_1_.getHasNormals();
    }

    /**
     * Clears the tessellator state in preparation for new drawing.
     */
    private void reset()
    {
        this.vertexCount = 0;
        this.byteBuffer.clear();
        this.rawBufferIndex = 0;
        this.addedVertices = 0;
    }

    /**
     * Sets draw mode in the tessellator to draw quads.
     */
    public void startDrawingQuads()
    {
        this.startDrawing(7);
    }

    /**
     * Resets tessellator state and prepares for drawing (with the specified draw mode).
     */
    public void startDrawing(int p_78371_1_)
    {
        if (this.isDrawing)
        {
            throw new IllegalStateException("Already tesselating!");
        }
        else
        {
            this.isDrawing = true;
            this.reset();
            this.drawMode = p_78371_1_;
            this.hasNormals = false;
            this.hasColor = false;
            this.hasTexture = false;
            this.hasBrightness = false;
            this.isColorDisabled = false;
        }
    }

    /**
     * Sets the texture coordinates.
     */
    public void setTextureUV(double p_78385_1_, double p_78385_3_)
    {
        this.hasTexture = true;
        this.textureU = p_78385_1_;
        this.textureV = p_78385_3_;
    }

    public void setBrightness(int p_78380_1_)
    {
        this.hasBrightness = true;
        this.brightness = p_78380_1_;
    }

    /**
     * Sets the RGB values as specified, converting from floats between 0 and 1 to integers from 0-255.
     */
    public void setColorOpaque_F(float p_78386_1_, float p_78386_2_, float p_78386_3_)
    {
        this.setColorOpaque((int)(p_78386_1_ * 255.0F), (int)(p_78386_2_ * 255.0F), (int)(p_78386_3_ * 255.0F));
    }

    /**
     * Sets the RGBA values for the color, converting from floats between 0 and 1 to integers from 0-255.
     */
    public void setColorRGBA_F(float p_78369_1_, float p_78369_2_, float p_78369_3_, float p_78369_4_)
    {
        this.setColorRGBA((int)(p_78369_1_ * 255.0F), (int)(p_78369_2_ * 255.0F), (int)(p_78369_3_ * 255.0F), (int)(p_78369_4_ * 255.0F));
    }

    /**
     * Sets the RGB values as specified, and sets alpha to opaque.
     */
    public void setColorOpaque(int p_78376_1_, int p_78376_2_, int p_78376_3_)
    {
        this.setColorRGBA(p_78376_1_, p_78376_2_, p_78376_3_, 255);
    }

    /**
     * Sets the RGBA values for the color. Also clamps them to 0-255.
     */
    public void setColorRGBA(int p_78370_1_, int p_78370_2_, int p_78370_3_, int p_78370_4_)
    {
        if (!this.isColorDisabled)
        {
            if (p_78370_1_ > 255)
            {
                p_78370_1_ = 255;
            }

            if (p_78370_2_ > 255)
            {
                p_78370_2_ = 255;
            }

            if (p_78370_3_ > 255)
            {
                p_78370_3_ = 255;
            }

            if (p_78370_4_ > 255)
            {
                p_78370_4_ = 255;
            }

            if (p_78370_1_ < 0)
            {
                p_78370_1_ = 0;
            }

            if (p_78370_2_ < 0)
            {
                p_78370_2_ = 0;
            }

            if (p_78370_3_ < 0)
            {
                p_78370_3_ = 0;
            }

            if (p_78370_4_ < 0)
            {
                p_78370_4_ = 0;
            }

            this.hasColor = true;

            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
            {
                this.color = p_78370_4_ << 24 | p_78370_3_ << 16 | p_78370_2_ << 8 | p_78370_1_;
            }
            else
            {
                this.color = p_78370_1_ << 24 | p_78370_2_ << 16 | p_78370_3_ << 8 | p_78370_4_;
            }
        }
    }

    public void func_154352_a(byte p_154352_1_, byte p_154352_2_, byte p_154352_3_)
    {
        this.setColorOpaque(p_154352_1_ & 255, p_154352_2_ & 255, p_154352_3_ & 255);
    }

    /**
     * Adds a vertex specifying both x,y,z and the texture u,v for it.
     */
    public void addVertexWithUV(double p_78374_1_, double p_78374_3_, double p_78374_5_, double p_78374_7_, double p_78374_9_)
    {
        this.setTextureUV(p_78374_7_, p_78374_9_);
        this.addVertex(p_78374_1_, p_78374_3_, p_78374_5_);
    }

    /**
     * Adds a vertex with the specified x,y,z to the current draw call. It will trigger a draw() if the buffer gets
     * full.
     */
    public void addVertex(double p_78377_1_, double p_78377_3_, double p_78377_5_)
    {
        ++this.addedVertices;

        if (this.hasTexture)
        {
            this.rawBuffer[this.rawBufferIndex + 3] = Float.floatToRawIntBits((float)this.textureU);
            this.rawBuffer[this.rawBufferIndex + 4] = Float.floatToRawIntBits((float)this.textureV);
        }

        if (this.hasBrightness)
        {
            this.rawBuffer[this.rawBufferIndex + 7] = this.brightness;
        }

        if (this.hasColor)
        {
            this.rawBuffer[this.rawBufferIndex + 5] = this.color;
        }

        if (this.hasNormals)
        {
            this.rawBuffer[this.rawBufferIndex + 6] = this.normal;
        }

        this.rawBuffer[this.rawBufferIndex + 0] = Float.floatToRawIntBits((float)(p_78377_1_ + this.xOffset));
        this.rawBuffer[this.rawBufferIndex + 1] = Float.floatToRawIntBits((float)(p_78377_3_ + this.yOffset));
        this.rawBuffer[this.rawBufferIndex + 2] = Float.floatToRawIntBits((float)(p_78377_5_ + this.zOffset));
        this.rawBufferIndex += 8;
        ++this.vertexCount;

        if (this.vertexCount % 4 == 0 && this.rawBufferIndex >= this.bufferSize - 32)
        {
            this.draw();
            this.isDrawing = true;
        }
    }

    /**
     * Sets the color to the given opaque value (stored as byte values packed in an integer).
     */
    public void setColorOpaque_I(int p_78378_1_)
    {
        int var2 = p_78378_1_ >> 16 & 255;
        int var3 = p_78378_1_ >> 8 & 255;
        int var4 = p_78378_1_ & 255;
        this.setColorOpaque(var2, var3, var4);
    }

    /**
     * Sets the color to the given color (packed as bytes in integer) and alpha values.
     */
    public void setColorRGBA_I(int p_78384_1_, int p_78384_2_)
    {
        int var3 = p_78384_1_ >> 16 & 255;
        int var4 = p_78384_1_ >> 8 & 255;
        int var5 = p_78384_1_ & 255;
        this.setColorRGBA(var3, var4, var5, p_78384_2_);
    }

    /**
     * Disables colors for the current draw call.
     */
    public void disableColor()
    {
        this.isColorDisabled = true;
    }

    /**
     * Sets the normal for the current draw call.
     */
    public void setNormal(float p_78375_1_, float p_78375_2_, float p_78375_3_)
    {
        this.hasNormals = true;
        byte var4 = (byte)((int)(p_78375_1_ * 127.0F));
        byte var5 = (byte)((int)(p_78375_2_ * 127.0F));
        byte var6 = (byte)((int)(p_78375_3_ * 127.0F));
        this.normal = var4 & 255 | (var5 & 255) << 8 | (var6 & 255) << 16;
    }

    /**
     * Sets the translation for all vertices in the current draw call.
     */
    public void setTranslation(double p_78373_1_, double p_78373_3_, double p_78373_5_)
    {
        this.xOffset = p_78373_1_;
        this.yOffset = p_78373_3_;
        this.zOffset = p_78373_5_;
    }

    /**
     * Offsets the translation for all vertices in the current draw call.
     */
    public void addTranslation(float p_78372_1_, float p_78372_2_, float p_78372_3_)
    {
        this.xOffset += (double)p_78372_1_;
        this.yOffset += (double)p_78372_2_;
        this.zOffset += (double)p_78372_3_;
    }
}
