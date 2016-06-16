package br.ufpe.integrativocbr.plugin;

import java.net.URI;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class NewAxiom {

	private OWLClass owlClass1;
	private OWLObjectProperty property;
	private OWLClass owlClass2;
	private OWLOntology ontology;

	public NewAxiom(OWLClass owlClass1, OWLObjectProperty property,
			OWLClass owlClass2, OWLOntology ontology) {
		this.owlClass1 = owlClass1;
		this.property = property;
		this.owlClass2 = owlClass2;
		this.ontology = ontology;
	}

	public NewAxiom() {
	}

	public OWLClass getOwlClass1() {
		return owlClass1;
	}

	public void setOwlClass1(OWLClass owlClass1) {
		this.owlClass1 = owlClass1;
	}

	public OWLObjectProperty getProperty() {
		return property;
	}

	public void setProperty(OWLObjectProperty property) {
		this.property = property;
	}

	public OWLClass getOwlClass2() {
		return owlClass2;
	}

	public void setOwlClass2(OWLClass owlClass2) {
		this.owlClass2 = owlClass2;
	}

	private String getOWLEntityLabel(OWLEntity owlEntity) {
		for (OWLAnnotation annotation : owlEntity.getAnnotations(ontology,
				OWLManager.getOWLDataFactory().getRDFSLabel())) {
			if (annotation.getValue() instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) annotation.getValue();
				return val.getLiteral();
			}
		}
		return "";
	}

    private String getShortForm(IRI uri){
        try {
            String rendering = uri.getFragment();
            if (rendering == null) {
                // Get last bit of path
                String path = uri.toURI().getPath();
                if (path == null) {
                    return uri.toString();
                }
                return uri.toURI().getPath().substring(path.lastIndexOf("/") + 1);
            }
            return rendering;
        }
        catch (Exception e) {
            return "<Error! " + e.getMessage() + ">";
        }
    }

	
	private String getOWLEntityLabelFormatted(OWLEntity owlEntity) {
		return getOWLEntityLabel(owlEntity).replace(" ", "_");
	}
	
//	private String getURIPrefix(URI uri) {
//		ontology.getDirectImports()
//	}
	
	private String getPrefix(URI uri) {
		if (uri.toString().contains("btl2")) {
			return "btl2";
		} else {
			return uri.toString();
		}
	}
	
	private String getTableName(OWLClass owlClass) {
		String label = getOWLEntityLabelFormatted(owlClass);
		if (label.equals("cellular_organism")) {
			return "organism";
		} else if (label.equals("protein")) {
			return "protein_name";
		} else if (label.equals("protein_coding_gene")) {
			return "gene_name";
		} else if (label.equals("molecular_entity")) {
			return "molecule";
		}
		return label;
	}
	
	@Override
	public String toString() {
		String owlClassTable1 = getTableName(owlClass1);
		String owlClassTable2 = getTableName(owlClass2);
		URI propertyURI = property.getIRI().toURI();
		
		StringBuilder out = new StringBuilder();
		out.append("map:");
		out.append(owlClassTable1);
		out.append("_");
		out.append(owlClassTable2);
		out.append("_");
		out.append(propertyURI.getFragment());
		out.append(" a d2rq:PropertyBridge;\n\td2rq:belongsToClassMap map:");
		out.append(owlClassTable1);
		out.append(";\n\td2rq:refersToClassMap map:");
		out.append(owlClassTable2);
		out.append(";\n\td2rq:property ");
		out.append(getPrefix(property.getIRI().toURI()));
		out.append(":");
		out.append(propertyURI.getFragment());
		out.append(";\n\td2rq:join \"");
		out.append(owlClassTable1);
		out.append(".id => ");
		out.append(owlClassTable2);
		out.append(".id\";\n\t.");
		return out.toString();
	}
}
