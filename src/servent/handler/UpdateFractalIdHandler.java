package servent.handler;

import app.AppConfig;
import app.CoordinatesModel;
import servent.message.Message;
import servent.message.UpdateFractalldMessage;
import servent.message.WakeIdleNodesMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class UpdateFractalIdHandler implements MessageHandler{

    private Message clientMessage;

    public UpdateFractalIdHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {

        UpdateFractalldMessage updateFractaIldMessage = (UpdateFractalldMessage) clientMessage;
        synchronized (AppConfig.fractalIdLock)
        {
            AppConfig.timestampedStandardPrint("dobili smo fractal id update od " + clientMessage.getSenderPort() + " za index " +updateFractaIldMessage.getIndex());
            //AppConfig.fractalIds.set(updateFractaIldMessage.getIndex(), updateFractaIldMessage.getValue());
            AppConfig.fractalIds.add(updateFractaIldMessage.getValue());
            AppConfig.portPerFractalId.add(updateFractaIldMessage.getSenderPort());

            //todo logika za budjenje
            //AppConfig.timestampedStandardPrint("Ispred Logike za budjenje-------------------------------------------");
            if(updateFractaIldMessage.isLastInSubFractal())
            {
                //AppConfig.timestampedStandardPrint("Dobio sam poruku ciji cvor je poslednji za sublevel: " + updateFractaIldMessage.getSenderPort());
                int index=-1;
                String fractalIdPrefix;
                String fractalId = AppConfig.fractalIds.get(updateFractaIldMessage.getIndex());
                if(fractalId.length()<2)
                {
                    index = 0;
                    fractalIdPrefix="";
                }
                else
                {
                    fractalIdPrefix = fractalId.substring(0,fractalId.length()-1);

                    for (int i=0;i<AppConfig.fractalIds.size();i++)
                    {
                        String id = AppConfig.fractalIds.get(i);
                        id = id.substring(0,id.length()-1);
                        if(fractalIdPrefix.equals(id))
                        {
                            index=i;
                            break;
                        }
                    }
                }

                if (AppConfig.myServentInfo.getListenerPort()==AppConfig.portPerFractalId.get(index))
                {
                    //AppConfig.timestampedStandardPrint("Treba da podelim koordinate drugima");
                    synchronized (AppConfig.startingCoordinatesLock)
                    {
                        //todo odredim sebi nove startne
                        int helper = 0;
                        ArrayList<CoordinatesModel> myNewCoordinates = calculateNewStartingPoints(helper);
                        helper++;
                        for (int i = index+1;i<AppConfig.fractalIds.size();i++)
                        {
                            String id = AppConfig.fractalIds.get(i);
                            //AppConfig.timestampedStandardPrint("id " + id);
                            id = id.substring(0,id.length()-1);
                            //AppConfig.timestampedStandardPrint("substring id " + id);
                            if(fractalIdPrefix.equals(id))
                            {
                                ArrayList<CoordinatesModel> newCoordinates = calculateNewStartingPoints(helper);
                                //AppConfig.timestampedStandardPrint("Saljem wake up cvoru" + AppConfig.portPerFractalId.get(i));
                                MessageUtil.sendMessage(new WakeIdleNodesMessage(AppConfig.myServentInfo.getListenerPort(),AppConfig.portPerFractalId.get(i),newCoordinates));
                                helper++;
                            }
                        }
                        AppConfig.myStartingCoordinates.clear();
                        AppConfig.myStartingCoordinates.addAll(myNewCoordinates);
                    }
                }

                if (updateFractaIldMessage.isLastInLvl())
                {
                    AppConfig.addZeroesToFractalIds();
                }

                AppConfig.timestampedStandardPrint("FractalId lista na kraju update-a: ");
                for (int i=0; i < AppConfig.fractalIds.size();i++)
                {
                    AppConfig.timestampedStandardPrint(AppConfig.fractalIds.get(i) + " - " + AppConfig.portPerFractalId.get(i));
                }
            }
        }
    }

    private ArrayList<CoordinatesModel> calculateNewStartingPoints(int helper)
    {
        ArrayList<CoordinatesModel> newCoordinates = new ArrayList<>();
        int x1 = AppConfig.myStartingCoordinates.get(helper).getX();
        int y1 = AppConfig.myStartingCoordinates.get(helper).getY();
        for(int i = 0; i < AppConfig.myStartingCoordinates.size(); i++)
        {
            if(i==helper)
            {
                newCoordinates.add(AppConfig.myStartingCoordinates.get(i));
            }
            else
            {
                int x2 = AppConfig.myStartingCoordinates.get(i).getX();
                int y2 = AppConfig.myStartingCoordinates.get(i).getY();
                newCoordinates.add(calculateCoordinates(x1,y1,x2,y2));
            }
        }
        return newCoordinates;
    }


    private CoordinatesModel calculateCoordinates(int x1, int y1, int x2, int y2)
    {
        int finalX;
        int finalY;
        finalX = (int)(x1 + AppConfig.proportion2*(x2-x1));
        finalY = (int)(y1 + AppConfig.proportion2*(y2-y1));
        CoordinatesModel coordinatesModel = new CoordinatesModel(finalX,finalY);
        return coordinatesModel;
    }
}
