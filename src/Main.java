import sac.State;
import sac.StateFunction;
import sac.graph.BestFirstSearch;
import sac.graph.GraphSearchConfigurator;
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
        String[] sudoku_gen =("................").split("\n\n"); // Generate sudoku (web: QQWing)
        long avr_time = 0;
        int sum_open = 0;
        int sum_closed = 0;
        int sum_solution = 0;
        for(String sgen : sudoku_gen)
        {
            SudokuState obj = new SudokuState(2);
            obj.fromString(sgen);
            System.out.println(obj.toString());
            System.out.println("Valid : " + obj.isValid() + "\n");
            obj.setHFunction(new HeurystykaPusteKomorki());
            long startTime = System.currentTimeMillis();
            BestFirstSearch bfs = new BestFirstSearch();
            bfs.setInitial(obj);

            GraphSearchConfigurator config = new GraphSearchConfigurator();
            config.setWantedNumberOfSolutions(Integer.MAX_VALUE);
            //config.setTimeLimit(5000);
            bfs.setConfigurator(config);

            bfs.execute();
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            System.out.println("Time taken: " + elapsedTime + " ms");
            System.out.println("States in Open set: " + bfs.getOpenSet().size());
            System.out.println("States in Closed set: " + bfs.getClosedSet().size());
            System.out.println("Number of solutions: " + bfs.getSolutions().size());
            System.out.println(bfs.getSolutions());
            avr_time += elapsedTime;
            sum_open += bfs.getOpenSet().size();
            sum_closed += bfs.getClosedSet().size();
            sum_solution += bfs.getSolutions().size();
        }
        System.out.println("Average time taken: " + avr_time/sudoku_gen.length + " ms");
        System.out.println("Open set sum: " + sum_open);
        System.out.println("Closed set sum: " + sum_closed);
        System.out.println("Solution sum: " + sum_solution);
    }
}