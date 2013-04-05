package commands;

public class BarcodeCommand {

    // First ints of arrays are for robot 0 (above: he will belong to team 0,
    // below: he will belong to team 1)
    // Second ints of arrays are for robot 1 (above: he will belong to team 0,
    // below: he will belong to team 1)
    // ...
    // Een correct bord bevat van elke array 2 ints met verschillende index (bv
    // barcodes 4,1,2,7 bij deze barcodes, dan zal robot 1 en 4 een team vormen
    // en robot 2 en 3 ook.
    public static final int[] TREASURE_TEAM0 = new int[] { 0, 1, 2, 3 };
    public static final int[] TREASURE_TEAM0_INVERSE = new int[] {
            calcInv(TREASURE_TEAM0[0]), calcInv(TREASURE_TEAM0[1]),
            calcInv(TREASURE_TEAM0[2]), calcInv(TREASURE_TEAM0[3]) };
    public static final int[] TREASURE_TEAM1 = new int[] { 4, 5, 6, 7 };
    public static final int[] TREASURE_TEAM1_INVERSE = new int[] {
            calcInv(TREASURE_TEAM1[0]), calcInv(TREASURE_TEAM1[1]),
            calcInv(TREASURE_TEAM1[2]), calcInv(TREASURE_TEAM1[3]) };

    public static final int[] SEESAW_START = new int[] { 11, 15, 19 };
    public static final int[] SEESAW_START_INVERSE = new int[] {
            calcInv(SEESAW_START[0]), calcInv(SEESAW_START[1]),
            calcInv(SEESAW_START[2]) };
    public static final int[] SEESAW_END = new int[] { 13, 17, 21 };
    public static final int[] SEESAW_END_INVERSE = new int[] {
            calcInv(SEESAW_END[0]), calcInv(SEESAW_END[1]),
            calcInv(SEESAW_END[2]) };

    private static int calcInv(int value) {
        int[] number = new int[6];
        int oldValue = value;
        int newValue = value % 32;
        if (newValue == oldValue) {
            number[0] = 0;
        } else {
            number[0] = 1;
        }
        oldValue = newValue;
        newValue = newValue % 16;
        if (newValue == oldValue) {
            number[1] = 0;
        } else {
            number[1] = 1;
        }
        oldValue = newValue;
        newValue = newValue % 8;
        if (newValue == oldValue) {
            number[2] = 0;
        } else {
            number[2] = 1;
        }
        oldValue = newValue;
        newValue = newValue % 4;
        if (newValue == oldValue) {
            number[3] = 0;
        } else {
            number[3] = 1;
        }
        oldValue = newValue;
        newValue = newValue % 2;
        if (newValue == oldValue) {
            number[4] = 0;
        } else {
            number[4] = 1;
        }
        oldValue = newValue;
        newValue = newValue % 1;
        if (newValue == oldValue) {
            number[5] = 0;
        } else {
            number[5] = 1;
        }
        int newNumber = 1 * number[0] + 2 * number[1] + 4 * number[2] + 8
                * number[3] + 16 * number[4] + 32 * number[5];
        return newNumber;
    }
}

/*
 * Alle combinaties (getal en inverse getal) - NIET VERWIJDEREN! 0 0, 1 32, 2 16, 3
 * 48, 4 8, 5 40, 6 24, 7 56, 9 36, 10 20, 11 52, 12 12, 13 44, 14 28, 15 60, 17 34, 18 18, 19
 * 50, 21 42, 22 26, 23 58, 25 38, 27 54, 29 46, 30 30, 31 62, 33 33, 35 49, 37 41, 39 57, 43
 * 53, 45 45, 47 61, 51 51, 55 59, 63 63
 */