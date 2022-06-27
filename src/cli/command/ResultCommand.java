package cli.command;

import app.AppConfig;
import app.CoordinatesModel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ResultCommand implements  CLICommand{
    @Override
    public String commandName() {
        return "result";
    }

    @Override
    public void execute(String args) {
        AppConfig.timestampedStandardPrint("Spremam finalan rezultat----------------------------------------------");
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

        //todo crtanje
        AppConfig.timestampedStandardPrint("Pocinjem crtanje svih rezultata------------------------------------------");
        for (Map.Entry<Integer, ArrayList<CoordinatesModel>> entry : AppConfig.resultsPerPort.entrySet())
        {
            ArrayList<CoordinatesModel> list = entry.getValue();
            for (CoordinatesModel coordinatesModel : list )
            {
                graphics2D.drawOval(AppConfig.imageWidth/2+coordinatesModel.getX(),AppConfig.imageHeight/2-coordinatesModel.getY(), 1, 1);
            }
        }
        try {
            // retrieve image
            BufferedImage bi = bufferedImage;
            File outputfile = new File("final.png");
            ImageIO.write(bi, "png", outputfile);
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("crtanje final slike slike nije uspelo");
        }

    }
}
