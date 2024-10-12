import java.util.HashSet;

public class Main
{
    public static class SudokuState
    {
        private byte[][] field;
        private int n , n_puste;
        public SudokuState(int n)
        {
            field = new byte[n*n][n*n];
            this.n = n;
            n_puste = n * n * n * n;
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
        public boolean isValid(byte[] t)
        {
            HashSet<Byte> seen = new HashSet<>();
            for (byte value : t)
            {
                if (value != 0)
                {
                    if (!seen.add(value))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        public int getEmpty()
        {
            return n_puste;
        }
    }
    public static void main(String[] args)
    {
        SudokuState obj = new SudokuState(3);
        System.out.println(obj.toString());
        obj.fromString("5.2..6.9....49.2..........73.6..59..9.86......7.......2..9.3...68...7..3.5...4...");
        System.out.println(obj.toString());
        System.out.println(obj.getEmpty());
    }
}