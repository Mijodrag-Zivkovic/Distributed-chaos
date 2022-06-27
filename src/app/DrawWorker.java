package app;

import servent.message.Message;
import servent.message.SendResultsMessage;
import servent.message.UpdateFractalldMessage;
import servent.message.util.MessageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

public class DrawWorker implements Runnable,Cancellable{

    private volatile boolean working = true;
    private static volatile boolean idle = true;

    @Override
    public void run() {
        AppConfig.timestampedStandardPrint("start startovan lol");

        synchronized (AppConfig.fractalIdLock)
        {
            //todo pogledaj svoj fId niz, vidi koji fId se tebi dodeljuje, posalji update komsijama
            if(AppConfig.fractalIds.size()==0)
            {
                AppConfig.timestampedStandardPrint("prvi sam u fractalId nizu, nice");
                AppConfig.fractalIds.add("0");
                AppConfig.portPerFractalId.add(AppConfig.myServentInfo.getListenerPort());
                AppConfig.myFractalIdIndex = 0;
                //todo set starting coordinates
                AppConfig.myStartingCoordinates.addAll(AppConfig.startingCoordinates);
                //todo send update messages
                sendUpdateMessages(false,false);
                idle=false;
            }
            else
            {
                int i = 2;
                double maxNodesPerLevelIDouble = Math.pow(AppConfig.numberOfVertices,i-1);
                //AppConfig.timestampedStandardPrint("max nodes per level double " + maxNodesPerLevelIDouble);
                int maxNodesPerLevelI = (int) maxNodesPerLevelIDouble;
                int maxNodesPerLevelPrevious=1;
                //AppConfig.timestampedStandardPrint("max nodes per level int " + maxNodesPerLevelI);
                while(AppConfig.fractalIds.size()+1>maxNodesPerLevelI)
                {
                    maxNodesPerLevelPrevious = maxNodesPerLevelI;
                    i++;
                    //AppConfig.timestampedStandardPrint("Vrednost i " + i);
                    maxNodesPerLevelIDouble = Math.pow(AppConfig.numberOfVertices,i-1);
                    //AppConfig.timestampedStandardPrint("max nodes per level double " + maxNodesPerLevelIDouble);
                    maxNodesPerLevelI = (int) maxNodesPerLevelIDouble;
                    //AppConfig.timestampedStandardPrint("max nodes per level int " + maxNodesPerLevelI);
                }
                String lastFractalId = AppConfig.fractalIds.get(AppConfig.fractalIds.size()-1);
                if (i>2)
                {
                    lastFractalId = lastFractalId.substring(i-2);
                }
                AppConfig.timestampedStandardPrint("last fractal Id " + lastFractalId);
                try {
                    int helper = Integer.parseInt(lastFractalId);
                    helper++;
                    helper=helper%AppConfig.numberOfVertices;
                    if (helper == 0)
                    {
                        helper++;
                    }

                    //todo logika za prefix
                    int helper2 = AppConfig.fractalIds.size() - maxNodesPerLevelPrevious;
                    int indexForPrefix = helper2/(AppConfig.numberOfVertices-1);
                    String prefixId = AppConfig.fractalIds.get(indexForPrefix);
                    prefixId = prefixId.substring(0,prefixId.length()-1);
                    String newFractalId = prefixId.concat(String.valueOf(helper));
                    AppConfig.timestampedStandardPrint("moj fractal id je " + newFractalId);
                    AppConfig.fractalIds.add(newFractalId);
                    AppConfig.portPerFractalId.add(AppConfig.myServentInfo.getListenerPort());
                    AppConfig.myFractalIdIndex = AppConfig.fractalIds.size()-1;
                    AppConfig.timestampedStandardPrint("moj fractal index je " + AppConfig.myFractalIdIndex);

                    //todo new fractal id = lastfractalid cut last, add helper instead of last
                    if(helper%(AppConfig.numberOfVertices-1)==0)
                    {
                        AppConfig.timestampedStandardPrint("necu biti idle--------------------------------");
                    }
                    else
                    {
                        AppConfig.timestampedStandardPrint("idle sam---------------------------------------");

                    }
                    //todo proveri da li si cvor koji popunjava trenutni nivo, ako da dodaj svima nulu
                    AppConfig.timestampedStandardPrint("Helper " + helper);
                    boolean lastInLvl = (AppConfig.fractalIds.size() == maxNodesPerLevelI ? true : false);
                    boolean lastInSubFractal = ( (helper % (AppConfig.numberOfVertices-1))==0 ? true : false);
                    if(lastInSubFractal)
                        AppConfig.timestampedStandardPrint("Zadnji sam u subfraktalu -------------------------------");
                    sendUpdateMessages(lastInLvl,lastInSubFractal);
                    if (lastInLvl)
                        AppConfig.addZeroesToFractalIds();
                    AppConfig.timestampedStandardPrint("FractalId lista na kraju starta: ");
                    for (i=0; i < AppConfig.fractalIds.size();i++)
                    {
                        AppConfig.timestampedStandardPrint(AppConfig.fractalIds.get(i) + " - " + AppConfig.portPerFractalId.get(i));
                    }
                }catch (NumberFormatException e)
                {
                    AppConfig.timestampedErrorPrint("last fractal id je los");
                    //maybe exit ili nesto
                }

            }
        }

        while (idle) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (working == false) {
                return;
            }
        }
        AppConfig.timestampedStandardPrint("");
        AppConfig.timestampedStandardPrint("--------------------------------------------------------");
        AppConfig.timestampedStandardPrint("izasao iz idle stanja");
        AppConfig.timestampedStandardPrint("--------------------------------------------------------");
        AppConfig.timestampedStandardPrint("");
        BufferedImage bufferedImage = new BufferedImage(AppConfig.imageWidth,AppConfig.imageHeight,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setBackground(Color.WHITE);
        graphics2D.clearRect(0,0,AppConfig.imageWidth,AppConfig.imageHeight);
        graphics2D.setColor(Color.green);
        //todo drawing starting vertices
        for (CoordinatesModel coordinatesModel : AppConfig.startingCoordinates)
        {
            graphics2D.drawOval(AppConfig.imageWidth/2+coordinatesModel.getX(),AppConfig.imageHeight/2-coordinatesModel.getY()-1, 3, 3);
        }
        graphics2D.setColor(Color.blue);
        graphics2D.drawOval(AppConfig.imageWidth/2+AppConfig.startingPoint.getX(),AppConfig.imageHeight/2-AppConfig.startingPoint.getY()-1, 3, 3);
        graphics2D.setColor(Color.red);
        Random rand = new Random(System.currentTimeMillis());
        if(AppConfig.resultsPerPort.get(AppConfig.myServentInfo.getListenerPort())==null)
        {
            ArrayList<CoordinatesModel> resultList = new ArrayList<>();
            resultList.add(new CoordinatesModel(AppConfig.startingPoint.getX(), AppConfig.startingPoint.getY()));
            AppConfig.resultsPerPort.put(AppConfig.myServentInfo.getListenerPort(), resultList);
        }
        int brojac = 0;
        int begin = 0;
        while(working)
        {
            ArrayList<CoordinatesModel> resultList = AppConfig.resultsPerPort.get(AppConfig.myServentInfo.getListenerPort());
            CoordinatesModel currentPoint = resultList.get(resultList.size()-1);
            int verticeToGravitateTo = rand.nextInt(AppConfig.numberOfVertices);

            int x1,y1;
            synchronized (AppConfig.startingCoordinatesLock)
            {
               x1 = AppConfig.myStartingCoordinates.get(verticeToGravitateTo).getX();
               y1 = AppConfig.myStartingCoordinates.get(verticeToGravitateTo).getY();
            }
            int x2 = currentPoint.getX();
            int y2 = currentPoint.getY();
            CoordinatesModel coordinatesModel = calculateCoordinates(x1,y1,x2,y2);
            AppConfig.resultsPerPort.get(AppConfig.myServentInfo.getListenerPort()).add(coordinatesModel);
            graphics2D.drawOval(AppConfig.imageWidth/2+coordinatesModel.getX(),AppConfig.imageHeight/2-coordinatesModel.getY(), 1, 1);
            brojac++;

            if (brojac%25==0)
            {

                //AppConfig.timestampedStandardPrint("sacuvao 25 rezultata");
                //todo send result message
                if(AppConfig.myServentInfo.getListenerPort()!=1100)
                {
                    //AppConfig.timestampedStandardPrint("Saljem rezultate cvoru 1100");
                    ArrayList<CoordinatesModel> results = AppConfig.resultsPerPort.get(AppConfig.myServentInfo.getListenerPort());
                    ArrayList<CoordinatesModel> resultsToSend = new ArrayList<>();
                    for (int i = begin;i<brojac;i++)
                    {
                        resultsToSend.add(results.get(i));
                    }
                    MessageUtil.sendMessage(new SendResultsMessage(AppConfig.myServentInfo.getListenerPort(),1100,resultsToSend));
                }
                begin = brojac;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            // retrieve image
            BufferedImage bi = bufferedImage;
            File outputfile = new File(AppConfig.myServentInfo.getListenerPort() + ".png");
            ImageIO.write(bi, "png", outputfile);
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("crtanje slike nije uspelo");
        }
        //izmesticemo


        AppConfig.timestampedStandardPrint("start zavrsen");
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


    private void sendUpdateMessages(boolean lastInLevel, boolean lastInSubFractal)
    {
        //AppConfig.timestampedStandardPrint("Saljem update poruke komsijama");
        for (Integer neighbourPort : AppConfig.myServentInfo.getNeighborsPorts())
        {
            MessageUtil.sendMessage(new UpdateFractalldMessage(AppConfig.myServentInfo.getListenerPort(),neighbourPort,
                    AppConfig.myFractalIdIndex,new String(AppConfig.fractalIds.get(AppConfig.myFractalIdIndex)),lastInLevel,lastInSubFractal));
        }
    }


    public static void setIdle(boolean idle2){
        idle=idle2;
    }


    @Override
    public void stop() {
        this.working = false;
    }
}
