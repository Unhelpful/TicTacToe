/**
 Copyright 2013 Andrew Mahone

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package us.looking_glass.tictactoe;


public class LMSRankPlayer extends LMSPlayerBase {
    private static final long SerialVersionUID = 1;

    public LMSRankPlayer() {
        super(6);
    }

    protected byte[] features(Board board) {
        byte[] features = new byte[featureSize];
        features[0] = (byte) board.countPlayer(1);
        features[1] = (byte) board.countPlayer(2);
        for (int i = 0; i < 8; i++) {
            int count1 = board.countRow(i, 1);
            int count2 = board.countRow(i, 2);
            if (count2 == 0) {
                switch (count1) {
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
                switch (count2) {
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
        return features;
    }
}
