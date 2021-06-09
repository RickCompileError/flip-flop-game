import javax.swing.*;
import java.util.*;

public class GenerateImage{
	public static ImageIcon[] generate(int level, int quantity){
		ImageIcon[] img = new ImageIcon[quantity];
		for (int i=0;i<quantity/2;i++){
			img[i] = new ImageIcon("pic/"+String.valueOf(level)+String.valueOf(i)+".jpg");
			img[i+quantity/2] = new ImageIcon("pic/"+String.valueOf(level)+String.valueOf(i)+".jpg");
		}
		List<ImageIcon> imgList = Arrays.asList(img);
		Collections.shuffle(imgList);
		img = imgList.toArray(new ImageIcon[0]);
		return img;
	}
}	