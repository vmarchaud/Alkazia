package net.minecraft.server;

public class StepSoundSlime extends StepSound {

	public StepSoundSlime(String s, float f, float f1) {
		super(s, f, f1);
	}

	public String getBreakSound() {
		return "mob.slime.big";
	}

	public String getPlaceSound() {
		return "mob.slime.small";
	}
}