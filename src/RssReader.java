import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RssReader {

	private static String newsUrl = "http://www.sjtu.edu.cn/system/resource/code/rss/rssfeed.jsp?type=list&viewid=2583&mode=10&dbname=vsb&owner=753372696&ownername=wwwsjtu&contentid=1862&number=5";
	private static String personUrl = "http://news.sjtu.edu.cn/system/resource/code/rss/rssfeed.jsp?type=list&treeid=1006&viewid=1333&mode=10&dbname=vsb&owner=741155814&ownername=newsnet&contentid=1270&number=60";
	private static String NewsXml = "C:\\Users\\daliwang\\Desktop\\01\\news\\openNews\\data\\wb.xml";
	private static String NewsTitleXml = "C:\\Users\\daliwang\\Desktop\\01\\news\\myNews.xml";

	private static String scholarFolder = "C:\\Users\\daliwang\\Desktop\\08\\muban\\xzbj\\";
	private static String youthFolder = "C:\\Users\\daliwang\\Desktop\\08\\muban\\qczj\\";
	private static String tmpFile = "tmp.png";
	
	private final static int MAX_SCHILARNUM = 11;
	private final static int MAX_YOUTHNUM = 14;

	public static void main(String[] args) {
		RssReader reader = new RssReader();
		reader.genNews(newsUrl);
//		reader.genPerson(personUrl);
	}
	
	void drawIcon(String content,String saveFile){
		try {
//			int width = 823;
//			int height = 69;
			int word_x = 65;
			int word_y = 38;
			Font font = new Font("微软雅黑", Font.PLAIN, 30);
			Graphics2D g2d;
			BufferedImage image;
//			image = new BufferedImage(width, height,
//					BufferedImage.TYPE_INT_RGB);
//
//			//Set transparency
//			g2d = image.createGraphics();
//
//			image = g2d.getDeviceConfiguration().createCompatibleImage(width,
//					height, Transparency.TRANSLUCENT);
//			g2d.dispose();
			
			image = ImageIO.read(new File(tmpFile));
			g2d = image.createGraphics();
			g2d.setColor(new Color(0,0,0));
			g2d.setFont(font);
			g2d.drawString(content, word_x, word_y);
			g2d.dispose();

			ImageIO.write(image, "png", new File(saveFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<SyndFeed> readFeedXml(String urlStr) throws Exception {
		URL feedurl = new URL(urlStr); // 指定rss位置
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(feedurl));
		@SuppressWarnings("unchecked")
		List<SyndFeed> entries = feed.getEntries();
		return entries;
	}

	public void genNews(String urlStr) {
		try {
			List<SyndFeed> entries = readFeedXml(urlStr);

			PrintWriter pw = new PrintWriter(NewsXml, "utf-8");
			PrintWriter pw2 = new PrintWriter(NewsTitleXml, "utf-8");

			pw.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
			pw.println("<content head=\"社内通知\">");

			pw2.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			pw2.println("<News mWidth=\"540\" mHeight=\"100\" mColor=\"0x000000\">");
			for (int i = 0; i < entries.size(); i++) {
				SyndEntry entry = (SyndEntry) entries.get(i);
				DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
				String pubDate = df.format(entry.getPublishedDate());

				String url = entry.getLink().replaceAll("&amp;", "&");
				Document doc = Jsoup.connect(url).get();
				//PrintWriter pw3 = new PrintWriter(i + ".html", "utf-8");
				//pw3.println(doc.toString());
				//pw3.close();
				Elements title = doc.getElementsByClass("titlestyle2570");
				Element content = doc.getElementById("vsb_content");

				pw.println("<article>");
				pw.println("<![CDATA[<br/><br/>");
				pw.print("<p align=\"center\"><font size=\"14\" color=\"#999999\"></font><br/></p>");
				pw.print("<p align=\"center\"><font size=\"24\" color=\"#000000\"><B>"
						+ title.text() + "</B></font></p>");
				pw.print("<p align=\"center\"><font size=\"14\" color=\"#999999\">"
						+ pubDate + "</font><br/></p><p align=\"center\"></p>");
				pw.println(content.html().replaceFirst(
						"<p align=\"right\">.*</p>", ""));
				pw.println("]]>");
				pw.println("</article>");

				pw2.print("<content Time=\"4\"><![CDATA[");
				pw2.print(title.text());
				pw2.println("]]></content>");
			}
			pw.println("</content>");
			pw2.println("</News>");
			pw.close();
			pw2.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public void genPerson(String urlStr) {
		List<SyndFeed> entries;
		int scholarNum = 0;
		int youthNum = 0;
		try {
			entries = readFeedXml(urlStr);
			for (int i = 0; i < entries.size(); i++) {
				SyndEntry entry = (SyndEntry) entries.get(i);
				String url = entry.getLink().replaceAll("&amp;", "&");
				String folder = "";
				if (scholarNum == MAX_SCHILARNUM && youthNum == MAX_YOUTHNUM) {
					break;
				} else if (entry.getTitle().contains("[学者笔谈]")
						&& scholarNum < MAX_SCHILARNUM) {
					scholarNum++;
					folder = scholarFolder + "wenzi" + scholarNum + "\\";
					drawIcon(entry.getTitle().replace("[学者笔谈]", ""), scholarFolder+"myBtns\\"+scholarNum+".png");
					drawIcon(entry.getTitle().replace("[学者笔谈]", ""), scholarFolder+"myBtns\\d_"+scholarNum+".png");
					System.out.println(scholarNum);
				} else if (entry.getTitle().contains("[青春足迹]")
						&& youthNum < MAX_YOUTHNUM) {
					youthNum++;
					folder = youthFolder + "wenzi" + youthNum + "\\";
					drawIcon(entry.getTitle().replace("[青春足迹]", ""), youthFolder+"myBtns\\"+youthNum+".png");
					drawIcon(entry.getTitle().replace("[青春足迹]", ""), youthFolder+"myBtns\\d_"+youthNum+".png");
					System.out.println(youthNum);
				}

				if (!folder.equals("")) {

					//drawIcon(entry.getTitle(), folder+"");
					Document doc = Jsoup.connect(url).get();
					Element content = doc.getElementById("vsb_content");
					
					System.out.println(entry.getTitle());
					System.out.println(url);
					
					Elements imgs = content.select("img");
					delAllFile(folder+"img\\pic");
					downImgs(imgs, folder + "img\\pic\\");
					delTag(content);
					PrintWriter pw2 = new PrintWriter(folder + "pages.xml",
							"utf-8");
					pw2.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
					pw2.println("<Mypage stageW=\"1080\" stageH=\"1400\" slip=\"0\" doubleClick=\"0\" effect=\"1\" filter=\"0\" btn=\"all_btns.swf\" idBar=\"all_dots.swf\">");
					pw2.println("	<pages pW=\"900\" pH=\"1300\" img=\"img/001.png\">");
					pw2.println("   	<article aW=\"800\" aH=\"1100\" aColor=\"0x000000\" aSize=\"20\" >");
					pw2.println("			<![CDATA[<p align=\"center\"><font size=\"35\" color=\"#B31F2B\"><B>"
							+ entry.getTitle() + "</B></font></p>");
					pw2.println(content.html()
							.replaceAll("<[Ss][Tt][Rr][Oo][Nn][Gg]>", "<B>")
							.replaceAll("</[Ss][Tt][Rr][Oo][Nn][Gg]>", "</B>")
							.replace("\r", "\n"));
					pw2.println("\n\n\n\n\n\n\n\n\n]]>");
					pw2.println("       </article>");
					pw2.println("   </pages>");
					pw2.println("</Mypage>");
					pw2.close();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void downAImg(String imgSrc, String saveFile){
		URL imgUrl;
		try {

			int len = 100;
			int downLen = 0;
			do {
				FileOutputStream fout = new FileOutputStream(saveFile);
				downLen = 0;
				imgUrl = new URL(imgSrc);
				HttpURLConnection uc = (HttpURLConnection)imgUrl.openConnection();
				uc.connect();
				InputStream is = uc.getInputStream();
				len = uc.getContentLength();
				byte[] buf = new byte[1024];
				int readLen = 0;
				while ((readLen = is.read(buf)) != -1) {
					fout.write(buf,0, readLen);
					downLen += readLen;
				}
				is.close();
			} while (downLen < len);
			
		} catch (IOException e) {
			System.out.println("Pic download failed.");
			downAImg(imgSrc, saveFile);
		} 
	}
	
	public void compressPic(String srcImgFile, String outputFile, int newWidth, int newHeight){
		try{
			BufferedImage srcImg = ImageIO.read(new File(srcImgFile));
			FileOutputStream fout;
			
			int width = srcImg.getWidth();
			int height = srcImg.getHeight();
			width = newWidth;
			height = newHeight;
			BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			newImg.getGraphics().drawImage(srcImg.getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, null);
			fout = new FileOutputStream(outputFile);
			ImageIO.write(newImg, "jpg", fout);
			fout.close();
		}catch(IOException e){
			
		}
	}

	//the pic will be compressed with a max width of 800
	void downImgs(Elements imgs, String saveFolder) {
		int maxWidth = 700;
		int oldHeight, oldWidth, newWidth, newHeight = 0;
		newWidth = maxWidth;
		newHeight = 0;
		
		for (int j = 0; j < imgs.size(); j++) {

			Element img = imgs.get(j);
			String imgSrc = img.absUrl("src");
			String saveFile = saveFolder+imgSrc.substring(imgSrc.lastIndexOf("/")+1);
			try {

				downAImg(imgSrc, saveFile);
				
				BufferedImage bufImg = ImageIO.read(new File(saveFile));
				oldHeight = bufImg.getHeight();
				oldWidth = bufImg.getWidth();
				if (oldWidth > maxWidth) System.out.println("compressed");
				newWidth = oldWidth > maxWidth ? maxWidth : oldWidth;
				newHeight = newWidth * oldHeight / oldWidth;
//				compressPic(saveFile,saveFile, newWidth, newHeight);
			} catch (Exception e) {
				System.out.println("jpg compress failed");
			} 
			

			img.attr("src",
					"img/pic/" + imgSrc.substring(imgSrc.lastIndexOf("/") + 1));
			//img.attr("hspace", "100");
			img.attr("width", Integer.toString(newWidth));
			img.attr("height", Integer.toString(newHeight));
			img.attr("hspace", Integer.toString((800-newWidth)/2));

			img.after("<p></p>");
			for (int i = 200; i < newHeight; i += 60) {
				img.after("<br/>");
			}
			img.after("<p></p>");
		}
	}

	//delete some html tags
	void delTag(Element content) {
		Elements fonts = content.select("font");
		Element f;
		while (fonts.size() != 0) {
			f = fonts.get(0);
			f.after(f.html());
			f.remove();
			fonts = content.select("font");
		}
		for (Element p : content.select("p")) {
			if (!p.hasAttr("align")||!p.select("img").isEmpty()){
				p.after("<br/>" + p.html());
				p.remove();
			}
		}
	}
	
	//param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			temp = new File(path + File.separator + tempList[i]);
			if (temp.isFile()) {
				temp.delete();
				flag = true;
			}
		}
		return flag;
	}

}
