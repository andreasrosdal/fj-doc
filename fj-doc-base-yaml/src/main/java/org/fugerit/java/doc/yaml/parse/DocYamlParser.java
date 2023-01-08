package org.fugerit.java.doc.yaml.parse;

import java.io.Reader;

import org.fugerit.java.doc.base.facade.DocFacadeSource;
import org.fugerit.java.doc.base.model.DocBase;
import org.fugerit.java.doc.base.parser.AbstractDocParser;
import org.fugerit.java.doc.base.parser.DocValidationResult;
import org.fugerit.java.doc.json.parse.DocObjectMapperHelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class DocYamlParser extends AbstractDocParser {
	
	private DocObjectMapperHelper helper;
	
	public DocYamlParser() {
		super( DocFacadeSource.SOURCE_TYPE_YAML );
		this.helper = new DocObjectMapperHelper( new ObjectMapper( new YAMLFactory() ) );
	}
	
	private DocObjectMapperHelper getHelper() {
		return this.helper;
	}
	
	@Override
	protected DocValidationResult validateWorker(Reader reader) throws Exception {
		return this.getHelper().validateWorkerResult(reader);
	}

	@Override
	protected DocBase parseWorker(Reader reader) throws Exception {
		return this.getHelper().parse(reader);
	}
	
}