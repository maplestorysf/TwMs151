package tools;

import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class ConvertsOpcodes {

    public static void main(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();
        System.out.println("Welcome to the Operation Code convertor. \r\nYour opcodes will be converted into hexadecimal numbers, \r\nAnd they will be saved in a new text file.");
        RecvPacketOpcode.reloadValues();
        SendPacketOpcode.reloadValues();
        sb.append("RecvOps.txt converted to hex data:").append("\r\n");
        for (RecvPacketOpcode recv : RecvPacketOpcode.values()) {
            sb.append("\r\n").append(recv.name()).append(" = ").append(HexTool.getOpcodeToString(recv.getValue()));
        }
        System.out.println("\r\nPlease enter the file name of the text file the new opcodes will be saved into: \r\n");
        Scanner input = new Scanner(System.in);
        FileOutputStream out = new FileOutputStream(input.next() + ".txt", false);
        sb.append("SendOps.txt converted to hex data:").append("\r\n");
        for (SendPacketOpcode send : SendPacketOpcode.values()) {
            sb.append("\r\n").append(send.name()).append(" = ").append(HexTool.getOpcodeToString(send.getValue()));
        }
        System.out.println("\r\n\r\n");
        out.write(sb.toString().getBytes());
    }
}
