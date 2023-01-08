package org.fugerit.java.doc.lib.autodoc;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.fugerit.java.core.cfg.ConfigException;
import org.fugerit.java.core.io.StreamIO;
import org.fugerit.java.core.lang.helpers.ClassHelper;
import org.fugerit.java.core.util.filterchain.MiniFilterChain;
import org.fugerit.java.doc.base.config.DocException;
import org.fugerit.java.doc.base.config.DocInput;
import org.fugerit.java.doc.base.config.DocOutput;
import org.fugerit.java.doc.base.config.DocTypeHandler;
import org.fugerit.java.doc.base.facade.DocFacade;
import org.fugerit.java.doc.base.model.DocBase;
import org.fugerit.java.doc.base.process.DocProcessConfig;
import org.fugerit.java.doc.base.process.DocProcessContext;
import org.fugerit.java.doc.base.process.DocProcessData;
import org.fugerit.java.doc.freemarker.html.FreeMarkerHtmlFragmentTypeHandler;
import org.fugerit.java.doc.lib.autodoc.detail.AutodocDetailModel;
import org.fugerit.java.doc.lib.autodoc.parser.model.AutodocModel;

public class AutodocDocConfig {


	private final static String CONFIG_PATH = "fj_doc_lib_autodoc/doc-process-config.xml";
	
	private DocProcessConfig config;
	
	private AutodocDocConfig( DocProcessConfig config ) {
		this.config = config;
	}
	
	public static AutodocDocConfig newConfig() throws ConfigException {
		AutodocDocConfig autodocDocConfig = null;
		try ( InputStream is = ClassHelper.loadFromDefaultClassLoader(CONFIG_PATH) )  {
			DocProcessConfig config = DocProcessConfig.loadConfig( is );
			autodocDocConfig = new AutodocDocConfig( config );
		} catch (Exception e) {
			throw new ConfigException( "Error creating configuration : "+e, e );
		}
		return autodocDocConfig;
	}

	public static final String CHAIN_ID_AUTODOC = "autodoc";
	
	public static final String CHAIN_ID_AUTODOC_DETAIL = "autodoc_detail";
	
	public DocProcessConfig getConfig() {
		return config;
	}
	
	private void process( String chainId, DocProcessContext context, DocProcessData data ) throws Exception {
		MiniFilterChain chain = this.config.getChainCache( chainId );
		chain.apply( context , data );
	}
	
	public void processAutodoc(  AutodocModel autodocModel, DocTypeHandler handler, OutputStream os ) throws DocException {
		try {
			DocProcessData data = new DocProcessData();
			DocProcessContext context = DocProcessContext.newContext( AutodocModel.ATT_NAME, autodocModel );
			process( CHAIN_ID_AUTODOC , context, data );
			DocBase docBase = DocFacade.parse( data.getCurrentXmlReader() );
			DocInput docInput = DocInput.newInput( handler.getType() , docBase );
			DocOutput docOutput = DocOutput.newOutput( os );
			handler.handle( docInput , docOutput );
		} catch (Exception e) {
			throw new DocException( "Autodoc generation error : "+e, e );
		}
	}
	
	public void processAutodocHtmlDefault(  AutodocModel autodocModel, OutputStream os ) throws DocException {
		try ( ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
				InputStream templateStream = ClassHelper.loadFromDefaultClassLoader( "fj_doc_lib_autodoc/html_template/main_template.html" ) ) {
			this.processAutodoc( autodocModel, FreeMarkerHtmlFragmentTypeHandler.HANDLER, buffer );
			String templateText = StreamIO.readString( templateStream );
			String htmlContent = templateText.replace( "[CONTENT_AREA_TOKEN]" , new String( buffer.toByteArray() ) );
			os.write( htmlContent.getBytes() );
		} catch (Exception e) {
			throw new DocException( "Autodoc generation error : "+e, e );
		}
	}
	
	public void processAutodocDetail(  AutodocDetailModel autoDetailModel, DocTypeHandler handler, OutputStream os ) throws DocException {
		try {
			DocProcessData data = new DocProcessData();
			DocProcessContext context = DocProcessContext.newContext( AutodocDetailModel.ATT_NAME, autoDetailModel );
			process( CHAIN_ID_AUTODOC_DETAIL , context, data );
			DocBase docBase = DocFacade.parse( data.getCurrentXmlReader() );
			DocInput docInput = DocInput.newInput( handler.getType() , docBase );
			DocOutput docOutput = DocOutput.newOutput( os );
			handler.handle( docInput , docOutput );
		} catch (Exception e) {
			throw new DocException( "Autodoc detail generation error : "+e, e );
		}
	}
	
	public void processAutodocDetailHtmlDefault(  AutodocDetailModel autoDetailModel, OutputStream os ) throws DocException {
		try ( ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
				InputStream templateStream = ClassHelper.loadFromDefaultClassLoader( "fj_doc_lib_autodoc/html_template/main_template.html" ) ) {
			this.processAutodocDetail( autoDetailModel, FreeMarkerHtmlFragmentTypeHandler.HANDLER, buffer );
			String templateText = StreamIO.readString( templateStream );
			String htmlContent = templateText.replace( "[CONTENT_AREA_TOKEN]" , new String( buffer.toByteArray() ) );
			os.write( htmlContent.getBytes() );
		} catch (Exception e) {
			throw new DocException( "Autodoc detail generation error : "+e, e );
		}
	}
	
}