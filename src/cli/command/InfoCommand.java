package cli.command;

import app.AppConfig;

import java.util.HashMap;
import java.util.Map;

public class InfoCommand implements CLICommand {

	@Override
	public String commandName() {
		return "info";
	}

	@Override
	public void execute(String args) {
		AppConfig.timestampedStandardPrint("My info: " + AppConfig.myServentInfo);
		AppConfig.timestampedStandardPrint("Neighbors:");
		String neighbors = "";
		for (Integer neighbor : AppConfig.myServentInfo.getNeighborsPorts()) {
			neighbors += neighbor + " ";
		}
		AppConfig.timestampedStandardPrint(neighbors);
		Map<Integer, Boolean> check = new HashMap<>();
		for (int i = 0;i<AppConfig.SERVENT_COUNT;i++)
		{
			int helper = i*100 + 1100;
			check.put(helper, false);
		}
		check.put(AppConfig.myServentInfo.getListenerPort(),true);
		for (Integer neighbour : AppConfig.myServentInfo.getNeighborsPorts())
		{
			check.put(neighbour,true);
		}

		if (AppConfig.myServentInfo.getNeighborsPorts().size()==AppConfig.SERVENT_COUNT-1 && allGood(check))
			AppConfig.timestampedStandardPrint("Sve u redu sa komsijama");

	}

	public boolean allGood(Map<Integer, Boolean> check)
	{
		for (Map.Entry<Integer, Boolean> entry : check.entrySet())
		{
			if (entry.getValue()==false)
				return false;
		}
		return true;
	}

}
