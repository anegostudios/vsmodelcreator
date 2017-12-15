package at.vintagestory.modelcreator.enums;

public enum EnumEntityActivity
{
	None(0),
    Walk(1),
    Fly(2),
    SwimStill(4),
    SwimMove(32768),
    Glide(8),
    Sprint(16),
    Sneak(32),
    Jump(64),
    Sleep(128),
    Eat(256),
    HandAttack(512),
    Defend(1024),
    Hurt(2048),
    Die(4096),
    Dead(8192),
    Climb(16384),
    SwordAttack(65536),
    Idle(131072),
    ChairSitting (262144),
    FloorSitting (524288),
    SneakIdle (1048576),
    ;
	
    public int flag;
    
    EnumEntityActivity(int flag) {
    	this.flag = flag;
    }

	public int index()
	{
		EnumEntityActivity[] values = values();
		for (int i = 0; i < values.length; i++) {
			if (values[i] == this) return i;
		}
		return -1;
	}
}
