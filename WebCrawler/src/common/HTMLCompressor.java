package common;

import static common.LogManager.writeGenericLog;

public class HTMLCompressor {
	public HTMLCompressor() {
	}
	
	// Remove html content
	public static String removeHtmlComment(String htmlContent) {
		if (htmlContent == null)
			return null;
		
		String openComment = "<!--";
		String closeComment = "-->";
		String compressedHtmlContent = "";
		
		while (true) {
			int index = htmlContent.indexOf(openComment);
			
			if (index != -1) {
				String part = htmlContent.substring(0, index);
				compressedHtmlContent += part;
				
				int closeIndex = htmlContent.indexOf(closeComment);
				if (closeIndex != -1) {
					htmlContent = htmlContent.substring(closeIndex+closeComment.length());
				} else {
					break;
				}
			} else {
				compressedHtmlContent += htmlContent;
				break;
			}
		}
		
		return compressedHtmlContent;
	}
	
	// Remove white spaces and new lines
	public static String trimLineWhiteSpace(String htmlContent) {
		if (htmlContent == null)
			return null;
		
		String compressedHtml = "";
		String lines[] = htmlContent.split("\\r?\\n");
		
		for (int i = 0; i < lines.length; i++) {
			compressedHtml += lines[i].trim();
		}
		
		return compressedHtml;
	}
	
	public static String compressHtmlContent(String htmlContent) {
		if (htmlContent != null) {
			writeGenericLog("Original length = "+htmlContent.length());
			htmlContent = HTMLCompressor.removeHtmlComment(htmlContent);
			htmlContent = HTMLCompressor.trimLineWhiteSpace(htmlContent);
			writeGenericLog("Compressed length = "+htmlContent.length());
		} else {
			writeGenericLog("Error: attempted to compress null string");
		}
		
		return htmlContent;
	}
}
