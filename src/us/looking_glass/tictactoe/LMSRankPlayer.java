package us.looking_glass.tictactoe;

/**
 * Created with IntelliJ IDEA.
 * User: chshrcat
 * Date: 10/23/13
 * Time: 1:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class LMSRankPlayer extends LMSPlayerBase {
    private static final long SerialVersionUID = 1;
    public LMSRankPlayer() {
        super(7);
    }

    protected byte[] features(Board board) {
        byte[] features = new byte[featureSize];
        features[0] = (byte)board.countPlayer(1);
        features[1] = (byte)board.countPlayer(2);
        for (int i = 0; i < 8; i++) {
            int count1 = board.countRow(i, 1);
            int count2 = board.countRow(i, 2);
            if (count2 == 0) {
                switch(count1) {
                    case 3:
                        features[2]++;
                        break;
                    case 2:
                        features[1]++;
                        break;
                    case 1:
                        features[0]++;
                        break;
                    default:
                        break;
                }
            } else if (count1 == 0) {
                switch(count2) {
                    case 3:
                        features[5]++;
                        break;
                    case 2:
                        features[4]++;
                        break;
                    case 1:
                        features[3]++;
                        break;
                    default:
                        break;
                }
            }
        }
        if (board.stateID() == 4632)
            features[6]++;
        return features;
    }
}
