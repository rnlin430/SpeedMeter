package Meter.Speed;

public class MoveData {
	private long time;

	private double x;
	private double z;
	private double y;

	public MoveData(){
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public void setLocation(double x, double z) {
		this.x = x;
		this.z = z;
	}

	public void setLocation(double x, double z, double y) {
		this.x = x;
		this.z = z;
		this.y = y;
	}
}
