package virtualcpu;

import java.io.PrintStream;

/**
 *
 * @author Nicklas Molving
 */
public class Machine {

    private CPU cpu = new CPU();
    private Memory memory = new Memory();

    public void load(Program program) {
        int index = 0;
        for (int instr : program) {
            memory.set(index++, instr);
        }
    }

    public void tick() {
        int instr = memory.get(cpu.getIp());
        if (instr == 0b0000_0000) {
            // 0000 0000  NOP
            cpu.incIp();
            // cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_0001) {
            // 0000 0001 ADD A B
            System.out.println("inside add");
            cpu.setA(cpu.getA() + cpu.getB());
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_0010) {
            // 0000 0010 MULTIPLY A B
            cpu.setA(cpu.getA() * cpu.getB());
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_0011) {
            // 0000 0011 DIVIDE A B
            cpu.setA(cpu.getA() / cpu.getB());
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_0100) {
            // 0000 0100 TRUE A = 0
            cpu.setFlag(cpu.getA() == 0);
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_0101) {
            // 0000 0101 TRUE A < 0
            cpu.setFlag(cpu.getA() < 0);
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_0110) {
            // 0000 0110 TRUE A > 0
            cpu.setFlag(cpu.getA() > 0);
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_0111) {
            // 0000 0111 TRUE A != 0
            cpu.setFlag(cpu.getA() != 0);
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_1000) {
            // 0000 1000 TRUE A = B
            cpu.setFlag(cpu.getA() == cpu.getB());
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_1001) {
            // 0000 1001 TRUE A < B
            cpu.setFlag(cpu.getA() < cpu.getB());
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_1010) {
            // 0000 1010 TRUE A > B
            cpu.setFlag(cpu.getA() > cpu.getB());
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_1011) {
            // 0000 1011 TRUE A != B
            cpu.setFlag(cpu.getA() != cpu.getB());
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_1100) {
            // 0000 1100 ALWAYS
            cpu.setFlag(true);
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0000_1111) {
            // 0000 1111 HALT EXECT
            // OBS! This is probably not the best way to implement this, but will stop the program.
            System.exit(1);
        }  else if (instr == 0b0001_0100) {
            // 0001 0100 MOVE A TO B
            cpu.setB(cpu.getA());
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0001_0101) {
            // 0001 0101 MOVE B TO A
            cpu.setA(cpu.getB());
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0001_0110) {
            // 0001 0110 A++
            cpu.setA(cpu.getA() + 1);
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0001_0111) {
            // 0001 0111 A--
            cpu.setA(cpu.getA() - 1);
            cpu.setIp(cpu.getIp() + 1);
        } else if ((instr & 0b1111_0000) == 0b0001_0000) {
            // 0001 000r PUSH r
            int r = (instr & 0b0000_0001);
            cpu.setSp(cpu.getSp() - 1);
            if (r == cpu.A) {
                memory.set(cpu.getSp(), cpu.getA());
            } else {
                memory.set(cpu.getSp(), cpu.getB());
            }
            cpu.setIp(cpu.getIp() + 1);
        } else if ((instr & 0b1111_0010) == 0b0001_0010) {
            // 0001 001r POP r
            int r = (instr & 0b0000_0001);
            if (r == cpu.getA()) {
                cpu.setA(memory.get(cpu.getSp()));
            } else {
                cpu.setB(memory.get(cpu.getSp()));
            }
            cpu.setSp(cpu.getSp() + 1);
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0001_0100) {
            // 0001 0100 MOV A B
            cpu.setB(cpu.getA());
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0001_0101) {
            // 0001 0101 MOV B A
            cpu.setA(cpu.getB());
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0001_0110) {
            // 0001 0110 INC A
            cpu.setA(cpu.getA() + 1);
            cpu.setIp(cpu.getIp() + 1);
        } else if (instr == 0b0001_0111) {
            // 0001 0111
            cpu.setA(cpu.getA() - 1);
            cpu.setIp(cpu.getIp() + 1);
        } else if ((instr & 0b1111_1000) == 0b0001_1000) {
            // 0001 1ooo RTN +o
            int o = instr & 0b0000_0111;

            cpu.setIp(cpu.getSp());
            cpu.setSp(cpu.getSp() + 1);

            cpu.setSp(cpu.getSp() + o);

            cpu.setIp(cpu.getIp() + 1);
        } else if ((instr & 0b1111_0000) == 0b0010_0000) {
            // 0010 r ooo	MOV r o	   [SP + o] ← r; IP++
            System.out.println("inside binary");
            // 0010 1 011 MOV B (=1) +3  [SP +3] // Move register B to memory position of SP with offset 3
            // 00101011 finding instruction
            //    and
            // 11110000
            // --------
            // 00100000
            // 00101011 finding offset
            //    and
            // 00000111
            // --------
            // 00000011 = 3
            // 00101011 finding register
            //    and
            // 00001000
            // --------
            // 00001000 = 8
            //    >> 3
            // 00000001 = 1

            System.out.println(instr);
            int o = instr & 0b0000_0111;
            int r = (instr & 0b0000_1000) >> 3;
            System.out.println("o = " + o);
            System.out.println("r = " + r);
            System.out.println("SP = " + cpu.getSp());
            if (r == cpu.A) {
                memory.set(cpu.getSp() + o, cpu.getA());
            } else {
                memory.set(cpu.getSp() + o, cpu.getB());
            }
            cpu.setIp(cpu.getIp() + 1);
        } else if ((instr & 0b1111_0000) == 0b0011_0000) {
            // 0011 ooor MOV +3 B
            int o = instr & 0b0000_1110;
            int r = (instr & 0b0000_0001);
            if (r == cpu.A) {
                memory.set(cpu.getA(), cpu.getSp() + o);
            } else {
                memory.set(cpu.getB(), cpu.getSp() + o);
            }
            cpu.setIp(cpu.getIp() + 1);
        } else if ((instr & 0b1100_0000) == 0b0100_0000) {
            // 01vv vvvr
            int r = instr & 0b0000_0001;
            int v = instr & 0b0011_1110;
            if (r == cpu.A) {
                cpu.setA(v);
            } else {
                cpu.setB(v);
            }
            cpu.setIp(cpu.getIp() + 1);
        } else if((instr & 0b1000_0000) == 0b1000_0000){
            // 10aa aaaa JMP #a
        } else if((instr & 0b1100_0000) == 0b1100_0000){
            // 11aa aaaa CALL #a
        }
    }

    public void print(PrintStream out) {
        memory.print(out);
        out.println("-------------");
        cpu.print(out);
    }

}
