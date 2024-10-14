import sac.State;
import sac.StateFunction;
import sac.graph.BestFirstSearch;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Main
{
    public static class SudokuState extends GraphStateImpl
    {
        private byte[][] field;
        private int n , n_puste;
        public SudokuState(int n)
        {
            field = new byte[n*n][n*n];
            this.n = n;
            n_puste = n * n;
        }
        public SudokuState(SudokuState s)
        {
            this(s.n);
            this.n_puste = s.n_puste;
            for(int i=0;i<this.field.length;++i)
            {
                for(int j=0;j < this.field[i].length;++j)
                {
                    this.field[i][j] = s.field[i][j];
                }
            }
        }
        @Override
        public String toString()
        {
            StringBuilder s = new StringBuilder();
            for(int i = 0; i < field.length; i++)
            {
                for(int j = 0; j < field[i].length; j++)
                {
                    s.append(field[i][j]);
                }
                s.append("\n");
            }
            return s.toString();
        }
        public void fromString(String s)
        {
            int idx = 0;
            n_puste = 0;
            for (int i = 0; i < field.length; i++) {
                for (int j = 0; j < field[i].length; j++)
                {
                    char c = s.charAt(idx);
                    if (c == '.' || c == '0')
                    {
                        field[i][j] = 0;
                        n_puste++;
                    } else
                    {
                        field[i][j] = (byte) (c - '0');
                    }
                    idx++;
                }
            }
        }
        public boolean isValid()
        {
            HashSet<Byte> seen;

            for (int i = 0; i < field.length; i++)
            {
                seen = new HashSet<>();
                for (int j = 0; j < field[i].length; j++)
                {
                    byte rowValue = field[i][j];
                    if (rowValue != 0 && !seen.add(rowValue))
                    {
                        return false;
                    }
                }

                seen.clear();
                for (int j = 0; j < field.length; j++)
                {
                    byte colValue = field[j][i];
                    if (colValue != 0 && !seen.add(colValue))
                    {
                        return false;
                    }
                }
            }

            int sqrtN = (int) Math.sqrt(field.length);
            for (int boxRow = 0; boxRow < field.length; boxRow += sqrtN)
            {
                for (int boxCol = 0; boxCol < field.length; boxCol += sqrtN)
                {
                    seen = new HashSet<>();
                    for (int i = 0; i < sqrtN; i++)
                    {
                        for (int j = 0; j < sqrtN; j++)
                        {
                            byte value = field[boxRow + i][boxCol + j];
                            if (value != 0 && !seen.add(value))
                            {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
        public int getEmpty()
        {
            return n_puste;
        }

        @Override
        public List<GraphState> generateChildren()
        {
            List<GraphState> lst = new ArrayList<>();
            for(int i = 0; i < field.length; i++)
            {
                for(int j = 0; j < field[i].length; j++)
                {
                    if(field[i][j] == 0)
                    {
                        for(int k = 1; k <= n*n; k++)
                        {
                            field[i][j] = (byte)k;
                            n_puste--;
                            if(isValid())
                            {
                                lst.add(new SudokuState(this));
                            }
                            n_puste++;
                            field[i][j] = 0;
                        }
                        return lst;
                    }
                }
            }
            return lst;
        }

        @Override
        public boolean isSolution()
        {
            return n_puste == 0;
        }
        @Override
        public int hashCode()
        {
            return toString().hashCode();
        }
    }
    public static class HeurystykaPusteKomorki extends StateFunction
    {
        public double calculate(State s)
        {
            if(s instanceof SudokuState)
            {
                SudokuState ss = (SudokuState)s;
                return ss.n_puste;
            }
            else
            {
                return Double.NaN;
            }
        }
    }
    public static void main(String[] args)
    {
        SudokuState obj = new SudokuState(3);
        System.out.println(obj.toString());
        obj.fromString(".2.....46.....67....6...5.....928.65..............382.5.4....8.3.9..2....6249.37.");
        System.out.println(obj.toString());
        System.out.println("Valid : "+obj.isValid()+"\n");
        obj.setHFunction(new HeurystykaPusteKomorki());
        BestFirstSearch bfs = new BestFirstSearch();
        bfs.setInitial(obj);
        bfs.execute();
        System.out.println(bfs.getSolutions());
        // code here
    }
}