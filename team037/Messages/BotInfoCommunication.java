package team037.Messages;

import battlecode.common.RobotType;
import battlecode.common.Team;
import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;
import team037.Utilites.Utilities;

public class BotInfoCommunication extends Communication
{
    public int id;
    public Team team;
    public RobotType type;
    public int x;
    public int y;

    @Override
    public int[] getValues()
    {
        return new int[]{CommunicationType.toInt(opcode), Utilities.intFromType(type),
        Utilities.intFromTeam(team), id, x, y};
    }

    @Override
    public void setValues(int[] values)
    {
        opcode = CommunicationType.fromInt(values[0]);
        type = Utilities.typeFromInt(values[1]);
        team = Utilities.teamFromInt(values[2]);
        id = values[3];
        x = values[4];
        y = values[5];
    }

    @Override
    public int[] getLengths()
    {
        return new int[]{CommunicationUtilities.opcodeSize, CommunicationUtilities.typeSize,
        CommunicationUtilities.teamSize, CommunicationUtilities.valSize,
        CommunicationUtilities.locationSize, CommunicationUtilities.locationSize};
    }
}
