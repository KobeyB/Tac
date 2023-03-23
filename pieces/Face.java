package pieces;

public enum Face {

	ONE(1, 9), TWO(2, 7), THREE(3, 7), FOUR(4, 7), FIVE(5, 7), SIX(6, 7), SEVEN(7, 8),
    EIGHT(8, 7), NINE(9, 7), TEN(10, 7), TRICKSTER(11, 7), TWELVE(12, 7), THIRTEEN(13, 9),
    TAC(14, 4), TEUFEL(15, 1), KREIGER(16, 1), ENGEL(17, 1), NARR(18, 1);
    
    private int faceValue;
    private int amount;
     
    private Face(int faceValue, int amount){
        this.faceValue = faceValue;
        this.amount = amount;
    }
    
    public int getAmount() {
    	return amount;
    }
    
    public int getValue(){
        return faceValue;
    }
    
}
