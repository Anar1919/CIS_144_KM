import java.util.Scanner;

public class Part1 {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        System.out.print("Enter first number: ");
        int i1 = s.nextInt();
        System.out.print("Enter second number: ");
        int i2 = s.nextInt();
        System.out.print("Enter third number: ");
        int i3 = s.nextInt();
        System.out.print("Enter fourth number: ");
        int i4 = s.nextInt();
        System.out.print("Enter fifth number: ");
        int i5 = s.nextInt();

        int l = lowest(i1, i2, i3, i4, i5);
        int h = highest(i1, i2, i3, i4, i5);

        // Only modify variable names if necessary
        System.out.println("1. Lowest number: " + l);
        System.out.println("2. Highest number: " + h);
    }

    static int lowest(int i1, int i2, int i3, int i4, int i5) {
        int result = i1;
        if (result > i2) result = i2;
        if (result > i3) result = i3;
        if (result > i4) result = i4;
        if (result > i5) result = i5;
        return result;
    }

    static int highest(int i1, int i2, int i3, int i4, int i5) {
        int result = i1;
        if (result < i2) result = i2;
        if (result < i3) result = i3;
        if (result < i4) result = i4;
        if (result < i5) result = i5;
        return result;
    }
}
