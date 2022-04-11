package readbiomed.nlp.metamaplite;

import java.io.FileReader;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ne.type.NamedEntityMention;

import gov.nih.nlm.nls.metamap.document.FreeText;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import gov.nih.nlm.nls.ner.MetaMapLite;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import readbiomed.python.AnnotationApplication;

public class MetaMapLiteEngine extends JCasAnnotator_ImplBase implements Callable<Integer> {

	private static final String PARAM_CONFIG_FILE_NAME = "PARAM_CONFIG_FILE_NAME";

	@Parameters(index = "0", description = "MetaMap Lite configuration file name.")
	private String configFileName;

	@Parameters(index = "1", description = "Number of instances in the pool.", defaultValue = "8")
	private String poolInstanceNumber;

	private MetaMapLite metaMapLiteInst;

	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		Properties myProperties = MetaMapLite.getDefaultConfiguration();

		// Loading properties file in "config", overriding previously defined
		// properties.
		try {
			try (FileReader fr = new FileReader((String) context.getConfigParameterValue(PARAM_CONFIG_FILE_NAME))) {
				myProperties.load(fr);
			}

			metaMapLiteInst = new MetaMapLite(myProperties);
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {
			List<Entity> entityList = metaMapLiteInst
					.processDocument(FreeText.instantiateBioCDocument(jCas.getDocumentText()));

			for (Entity entity : entityList) {
				NamedEntityMention nem = new NamedEntityMention(jCas, entity.getStart(),
						entity.getStart() + entity.getLength());
				nem.setMentionType("MetaMapLite");
				nem.setScore(entity.getScore());

				StringBuilder buffer = new StringBuilder();
				for (Ev ev : entity.getEvSet()) {
					buffer.append(ev.getConceptInfo() + "|" + entity.getMatchedText() + ";");
				}
				nem.setMentionId(buffer.toString());
				nem.addToIndexes(jCas);
			}
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	public static AnalysisEngineDescription getDescription(String configFileName)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(MetaMapLiteEngine.class, PARAM_CONFIG_FILE_NAME,
				configFileName);
	}

	public static void main(String[] argc) throws UIMAException {
		new CommandLine(new MetaMapLiteEngine()).execute(argc);
	}

	@Override
	public Integer call() throws Exception {
		(new AnnotationApplication(MetaMapLiteEngine.getDescription(configFileName),
				Integer.parseInt(poolInstanceNumber))).startServer();
		return 0;
	}
}