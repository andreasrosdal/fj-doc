package org.fugerit.java.doc.json.parse;

import java.io.InputStream;
import java.util.Iterator;

import org.fugerit.java.doc.base.config.DocException;
import org.fugerit.java.doc.base.model.DocBase;
import org.fugerit.java.doc.base.model.DocContainer;
import org.fugerit.java.doc.base.model.DocPara;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DocJsonParser {

	private final static Logger logger = LoggerFactory.getLogger( DocJsonParser.class );
	
	private void handleElement( DocBase docBase, DocContainer currentContainer, JsonNode jsonNode, ObjectMapper mapper ) throws DocException {
		String type = jsonNode.get( "type" ).textValue();
		if ( "container".equalsIgnoreCase( type ) ) {
			JsonNode elements = jsonNode.get( "elements" );
			if ( elements.isArray() ) {
				Iterator<JsonNode> itElements = elements.iterator();
				while (  itElements.hasNext() ) {
					this.handleElement( docBase , currentContainer, itElements.next(), mapper);
				}
			} else {
				throw new DocException( "Property elements should be an array" );
			}
		} else if ( "para".equalsIgnoreCase( type ) ) {
			DocPara para = new DocPara();
			String text = jsonNode.get( "text" ).asText();
			para.setText( text );
			JsonNode style = jsonNode.get( "style" );
			if ( style != null ) {
				String styleValue = style.asText();
				if ( "bold".contentEquals( styleValue ) ) {
					para.setStyle( DocPara.STYLE_BOLD );
				}
			}
			currentContainer.addElement(  para );
			logger.info( "add para {}", text );
		}
	}
	
	public DocBase parse( InputStream is ) throws DocException {
		DocBase docBase = new DocBase();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree( is );
			JsonNode docNode = root.get( "doc" );
			JsonNode metadataNode = docNode.get( "metadata" );
			JsonNode bodyNode = docNode.get( "body" );
			logger.info( "metadataNode -> {}", metadataNode );
			logger.info( "bodyNode -> {}", bodyNode );
			this.handleElement(docBase, docBase.getDocBody(), bodyNode, mapper);
		} catch (DocException e) {
			throw e;
		} catch (Exception e) {
			throw new DocException( "Error parsing json document "+e, e );
		}
		return docBase;
	}
	
}
