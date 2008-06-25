import java.util.BitSet;

public class Encoder {

	private static int numbits = 6 * 5;


	public static void main(String[] args) {
		double[] latlon = new Encoder().decode("dj248j248j24");
		System.out.println(latlon[0] + " " + latlon[1]);
		
		Encoder e = new Encoder();
		String s = e.encode(30, -90.0);
		System.out.println(s);
	}

	public double[] decode(String geohash) {
		StringBuilder buffer = new StringBuilder();
		for (char c : geohash.toCharArray()) {

			int i = Base32.lookup.get(c) + 32;
			buffer.append( Integer.toString(i, 2).substring(1) );
		}
		
		BitSet lonset = new BitSet();
		BitSet latset = new BitSet();
		
		//even bits
		int j =0;
		for (int i=0; i< numbits*2;i+=2) {
			boolean isSet = false;
			if ( i < buffer.length() )
			  isSet = buffer.charAt(i) == '1';
			lonset.set(j++, isSet);
		}
		
		//odd bits
		j=0;
		for (int i=1; i< numbits*2;i+=2) {
			boolean isSet = false;
			if ( i < buffer.length() )
			  isSet = buffer.charAt(i) == '1';
			latset.set(j++, isSet);
		}
		
		double lon = decode(lonset, -180, 180);
		double lat = decode(latset, -90, 90);
		
		return new double[] {lat, lon};		
	}
	
	private double decode(BitSet bs, double floor, double ceiling) {
		double mid = 0;
		for (int i=0; i<bs.length(); i++) {
			mid = (floor + ceiling) / 2;
			if (bs.get(i))
				floor = mid;
			else
				ceiling = mid;
		}
		return mid;
	}
	
	
	public String encode(double lat, double lon) {
		BitSet latbits = getBits(lat, -90, 90);
		BitSet lonbits = getBits(lon, -180, 180);
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < numbits; i++) {
			buffer.append( (lonbits.get(i))?'1':'0');
			buffer.append( (latbits.get(i))?'1':'0');
		}
		return Base32.base32(Long.parseLong(buffer.toString(), 2));
	}

	private BitSet getBits(double lat, double floor, double ceiling) {
		BitSet buffer = new BitSet(numbits);
		for (int i = 0; i < numbits; i++) {
			double mid = (floor + ceiling) / 2;
			if (lat >= mid) {
				buffer.set(i);
				floor = mid;
			} else {
				ceiling = mid;
			}
		}
		return buffer;
	}



}
