package de.sudoq.external.dlx;

import java.util.List;

public class SamuraiSudokuHandler extends FastSudokuHandler {
    public SamuraiSudokuHandler(int boardSize, List<int[][]> solutions) {
        super(boardSize, solutions);
    }


    protected int[][] parseBoard(List<DancingLinks.DancingNode> answer){
        int[][] result = new int[size][size];
        for(DancingLinks.DancingNode n : answer){
            DancingLinks.DancingNode rcNode = n;
            int min = Integer.parseInt(rcNode.C.name);
            for(DancingLinks.DancingNode tmp = n.R; tmp != n; tmp = tmp.R){
                int val = Integer.parseInt(tmp.C.name);
                if (val < min){
                    min = val;
                    rcNode = tmp;
                }
            }
            int ans1 = Integer.parseInt(rcNode.C.name);
            int ans2 = Integer.parseInt(rcNode.R.C.name) -369;
            int[] rc = getCoordinates(ans1);
            int r = rc[0];
            int c = rc[1];
            if (r >= 21 || c >= 21)
                System.out.println("i: "+ans1+" r: "+r+" c: "+c);
            int num = (ans2 % 9) + 1;
            result[r][c] = num;
        }
        return result;
    }

    private int[] getCoordinates(int i){
        if (0<= i && i < 81){
            return getCoordinates9x9(i, 0,0);
        }
        else if (i < 81 * 2){
            return getCoordinates9x9(i % 81, 0,12);
        }
        else if (i < 81 * 3){
            return getCoordinates9x9(i % 81, 12,0);
        }
        else if (i < 81 * 4){
            return getCoordinates9x9(i % 81, 12,12);
        }
        else if (i < 81*4 + 9){
            i -=  81*4;
            return new int[]{7+ i/3, 10+ i%3};
        }
        else if (i < 81*4 + 9 + 3*9){
            i -=  81*4 + 9;
            return new int[]{10+ i/3, 7 + i%9};
        }
        else if (i < 81*4 + 9 + 3*9 + 9){
            i -=  81*4 + 9 + 3*9;
            return new int[]{13+ i/3, 10+ i%3};
        }
        else
            return null;

    }

    private int[] getCoordinates9x9(int i, int offsetR, int offsetC){
        return new int[]{offsetR + i/9, offsetC + i%9};
    }


}
