/*
 * (C) Copyright IBM Corp. 2019, 2021
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.generator;

import static org.linuxforhealth.fhir.model.r5.util.JsonSupport.JSON_OBJECT_MAPPER;
import static org.linuxforhealth.fhir.model.r5.util.JsonSupport.nonClosingOutputStream;
import static org.linuxforhealth.fhir.model.r5.util.JsonSupport.nonClosingWriter;
import static org.linuxforhealth.fhir.model.r5.util.ModelSupport.isPrimitiveType;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;

import org.eclipse.xtext.util.RuntimeIOException;
import org.linuxforhealth.fhir.model.generator.exception.FHIRGeneratorException;
import org.linuxforhealth.fhir.model.r5.resource.Resource;
import org.linuxforhealth.fhir.model.r5.type.*;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.visitor.Visitable;

import com.fasterxml.jackson.core.JsonGenerator;

/*
 * Modifications:
 * 
 * - Use Jackson to generate JSON output
 */

public class FHIRJsonGenerator extends FHIRAbstractGenerator {

    protected FHIRJsonGenerator(boolean prettyPrinting) {
        super(prettyPrinting);
    }

    @Override
    public void generate(Visitable visitable, OutputStream out) throws FHIRGeneratorException {
        GeneratingVisitor visitor = null;
        try (JsonGenerator generator = getGeneratorFactory(nonClosingOutputStream(out))) {
            visitor = new JsonGeneratingVisitor(generator);
            visitable.accept(visitor);
            generator.flush();
        } catch (Exception e) {
            throw new FHIRGeneratorException(e.getMessage(), (visitor != null) ? visitor.getPath() : null, e);
        }
    }

    @Override
    public void generate(Visitable visitable, Writer writer) throws FHIRGeneratorException {
        GeneratingVisitor visitor = null;
        try (JsonGenerator generator = getGeneratorFactory(nonClosingWriter(writer))) {
            visitor = new JsonGeneratingVisitor(generator);
            visitable.accept(visitor);
            generator.flush();
        } catch (Exception e) {
            throw new FHIRGeneratorException(e.getMessage(), (visitor != null) ? visitor.getPath() : null, e);
        }
    }

    @Override
    public boolean isPrettyPrinting() {
        return prettyPrinting;
    }

    private static class JsonGeneratingVisitor extends GeneratingVisitor {
        private final JsonGenerator generator;

        private JsonGeneratingVisitor(JsonGenerator generator) {
            this.generator = generator;
        }

        private void generate(Element element) {
            if (element.getId() != null) {
                // visit id
                visit("id", element.getId());
            }
            if (!element.getExtension().isEmpty()) {
                // visit extension
                visitStart("extension", element.getExtension(), Extension.class);
                int elementIndex = 0;
                for (Extension extension : element.getExtension()) {
                    extension.accept("extension", elementIndex++, this);
                }
                visitEnd("extension", element.getExtension(), Extension.class);
            }
        }

        private boolean hasIdOrExtension(Element element) {
            return element.getId() != null || !element.getExtension().isEmpty();
        }

        private boolean hasIdOrExtension(java.util.List<? extends Visitable> visitables) {
            for (Visitable visitable : visitables) {
                if (hasIdOrExtension((Element) visitable)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean visit(java.lang.String elementName, int elementIndex, Base64Binary base64Binary) {
            if (isChoiceElement(elementName)) {
                elementName = getChoiceElementName(elementName, Base64Binary.class);
            }
            if (base64Binary.getValue() != null) {
                writeValue(elementName, elementIndex, Base64.getEncoder().encodeToString(base64Binary.getValue()));
            } else {
                writeNull(elementName, elementIndex, base64Binary);
            }
            return false;
        }

        @Override
        public boolean visit(java.lang.String elementName, int elementIndex, Boolean _boolean) {
            if (isChoiceElement(elementName)) {
                elementName = getChoiceElementName(elementName, Boolean.class);
            }
            if (_boolean.getValue() != null) {
                writeValue(elementName, elementIndex, _boolean.getValue());
            } else {
                writeNull(elementName, elementIndex, _boolean);
            }
            return false;
        }

        @Override
        public boolean visit(java.lang.String elementName, int elementIndex, Date date) {
            if (isChoiceElement(elementName)) {
                elementName = getChoiceElementName(elementName, Date.class);
            }
            if (date.getValue() != null) {
                writeValue(elementName, elementIndex, Date.PARSER_FORMATTER.format(date.getValue()));
            } else {
                writeNull(elementName, elementIndex, date);
            }
            return false;
        }

        @Override
        public boolean visit(java.lang.String elementName, int elementIndex, DateTime dateTime) {
            if (isChoiceElement(elementName)) {
                elementName = getChoiceElementName(elementName, DateTime.class);
            }
            if (dateTime.getValue() != null) {
                writeValue(elementName, elementIndex, DateTime.PARSER_FORMATTER.format(dateTime.getValue()));
            } else {
                writeNull(elementName, elementIndex, dateTime);
            }
            return false;
        }

        @Override
        public boolean visit(java.lang.String elementName, int elementIndex, Decimal decimal) {
            if (isChoiceElement(elementName)) {
                elementName = getChoiceElementName(elementName, Decimal.class);
            }
            if (decimal.getValue() != null) {
                writeValue(elementName, elementIndex, decimal.getValue());
            } else {
                writeNull(elementName, elementIndex, decimal);
            }
            return false;
        }

        @Override
        public boolean visit(java.lang.String elementName, int elementIndex, Instant instant) {
            if (isChoiceElement(elementName)) {
                elementName = getChoiceElementName(elementName, Instant.class);
            }
            if (instant.getValue() != null) {
                writeValue(elementName, elementIndex, Instant.PARSER_FORMATTER.format(instant.getValue()));
            } else {
                writeNull(elementName, elementIndex, instant);
            }
            return false;
        }

        @Override
        public boolean visit(java.lang.String elementName, int elementIndex, Integer integer) {
            if (isChoiceElement(elementName)) {
                elementName = getChoiceElementName(elementName, integer.getClass());
            }
            if (integer.getValue() != null) {
                writeValue(elementName, elementIndex, integer.getValue());
            } else {
                writeNull(elementName, elementIndex, integer);
            }
            return false;
        }

        @Override
        public void doVisit(java.lang.String elementName, java.lang.String value) {
            writeValue(elementName, -1, value);
        }

        @Override
        public boolean visit(java.lang.String elementName, int elementIndex, String string) {
            if (isChoiceElement(elementName)) {
                elementName = getChoiceElementName(elementName, string.getClass());
            }
            if (string.getValue() != null) {
                writeValue(elementName, elementIndex, string.getValue());
            } else {
                writeNull(elementName, elementIndex, string);
            }
            return false;
        }

        @Override
        public boolean visit(java.lang.String elementName, int elementIndex, Time time) {
            if (isChoiceElement(elementName)) {
                elementName = getChoiceElementName(elementName, Time.class);
            }
            if (time.getValue() != null) {
                writeValue(elementName, elementIndex, Time.PARSER_FORMATTER.format(time.getValue()));
            } else {
                writeNull(elementName, elementIndex, time);
            }
            return false;
        }

        @Override
        public boolean visit(java.lang.String elementName, int elementIndex, Uri uri) {
            if (isChoiceElement(elementName)) {
                elementName = getChoiceElementName(elementName, uri.getClass());
            }
            if (uri.getValue() != null) {
                writeValue(elementName, elementIndex, uri.getValue());
            } else {
                writeNull(elementName, elementIndex, uri);
            }
            return false;
        }

        @Override
        public boolean visit(java.lang.String elementName, int elementIndex, Xhtml xhtml) {
            writeValue(elementName, elementIndex, xhtml.getValue());
            return false;
        }

        @Override
		public void visitStart(java.lang.String elementName, List<? extends Visitable> visitables, Class<?> type) {
		    if (!visitables.isEmpty()) {
		        writeStartArray(elementName);
		    }
		}

		@Override
        public void visitEnd(java.lang.String elementName, List<? extends Visitable> visitables, Class<?> type) {
			try {
	            if (!visitables.isEmpty()) {
	                generator.writeEndArray();
	                if (isPrimitiveType(type) && hasIdOrExtension(visitables)) {
	                	generator.writeFieldName("_" + elementName);
	                    generator.writeStartArray();
	                    for (Visitable visitable : visitables) {
	                        if (hasIdOrExtension((Element) visitable)) {
	                            generator.writeStartObject();
	                            generate((Element) visitable);
	                            generator.writeEndObject();
	                        } else {
	                            generator.writeNull();
	                        }
	                    }
	                    generator.writeEndArray();
	                }
	            }
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
        }

        @Override
        public void doVisitStart(java.lang.String elementName, int elementIndex, Element element) {
            Class<?> elementType = element.getClass();
            if (!isPrimitiveType(elementType)) {
                if (isChoiceElement(elementName)) {
                    elementName = getChoiceElementName(elementName, element.getClass());
                }
                writeStartObject(elementName, elementIndex);
            } else if (getDepth() == 1) {
                try {
					generator.writeStartObject();
				} catch (IOException e) {
					throw new RuntimeIOException(e);
				}
            }
        }

        @Override
		public void doVisitEnd(java.lang.String elementName, int elementIndex, Element element) {
        	try {
			    Class<?> elementType = element.getClass();
			    if (isPrimitiveType(elementType)) {
			        if (isChoiceElement(elementName)) {
			            elementName = getChoiceElementName(elementName, elementType);
			        }
			        if (elementIndex == -1 && hasIdOrExtension(element)) {
			        	generator.writeFieldName("_" + elementName);
			            generator.writeStartObject();
			            generate(element);
			            generator.writeEndObject();
			        }
			        if (getDepth() == 1) {
			            generator.writeEndObject();
			        }
			    } else {
			        generator.writeEndObject();
			    }
        	} catch (IOException e) {
        		throw new RuntimeIOException(e);
        	}
		}

		@Override
        public void doVisitStart(java.lang.String elementName, int elementIndex, Resource resource) {
            writeStartObject(elementName, elementIndex);
            Class<?> resourceType = resource.getClass();
            java.lang.String resourceTypeName = resourceType.getSimpleName();
            
            try {
				generator.writeStringField("resourceType", resourceTypeName);
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
        }

        @Override
		public void doVisitEnd(java.lang.String elementName, int elementIndex, Resource resource) {
		    try {
				generator.writeEndObject();
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
		}

		private void writeNull(java.lang.String elementName, int elementIndex, Element element) {
        	try {
	            if (elementIndex != -1 && hasIdOrExtension(element)) {
	                generator.writeNull();
	            }
	        } catch (IOException e) {
	    		throw new RuntimeIOException(e);
	    	}	            
        }

        private void writeStartArray(java.lang.String elementName) {
        	try {
	        	generator.writeFieldName(elementName);
	            generator.writeStartArray();
	        } catch (IOException e) {
	    		throw new RuntimeIOException(e);
	    	}
        }

        private void writeStartObject(java.lang.String elementName, int elementIndex) {
        	try {
	            if (getDepth() > 1 && elementIndex == -1) {
	            	generator.writeFieldName(elementName);
	                generator.writeStartObject();
	            } else {
	                generator.writeStartObject();
	            }
	        } catch (IOException e) {
	    		throw new RuntimeIOException(e);
	    	}
        }

        private void writeValue(java.lang.String elementName, int elementIndex, BigDecimal value) {
        	try {
	            if (elementIndex == -1) {
	                generator.writeNumberField(elementName, value);
	            } else {
	                generator.writeNumber(value);
	            }
	    	} catch (IOException e) {
	    		throw new RuntimeIOException(e);
	    	}
        }

        private void writeValue(java.lang.String elementName, int elementIndex, java.lang.Boolean value) {
        	try {
	            if (elementIndex == -1) {
	                generator.writeBooleanField(elementName, value);
	            } else {
	                generator.writeBoolean(value);
	            }
	    	} catch (IOException e) {
	    		throw new RuntimeIOException(e);
	    	}
        }

        private void writeValue(java.lang.String elementName, int elementIndex, java.lang.Integer value) {
        	try {
	            if (elementIndex == -1) {
	                generator.writeNumberField(elementName, value);
	            } else {
	                generator.writeNumber(value);
	            }
	    	} catch (IOException e) {
	    		throw new RuntimeIOException(e);
	    	}
        }

        private void writeValue(java.lang.String elementName, int elementIndex, java.lang.String value) {
        	try {
	            if (elementIndex == -1) {
	                generator.writeStringField(elementName, value);
	            } else {
	                generator.writeString(value);
	            }
        	} catch (IOException e) {
        		throw new RuntimeIOException(e);
        	}
        }
    }

    private JsonGenerator getGeneratorFactory(OutputStream out) throws IOException {
        final JsonGenerator generator = JSON_OBJECT_MAPPER.createGenerator(out);
		return prettyPrinting 
			? generator.useDefaultPrettyPrinter() 
			: generator.setPrettyPrinter(null);
    }
    
    private JsonGenerator getGeneratorFactory(Writer writer) throws IOException {
    	final JsonGenerator generator = JSON_OBJECT_MAPPER.createGenerator(writer);
    	return prettyPrinting 
			? generator.useDefaultPrettyPrinter() 
			: generator.setPrettyPrinter(null);
    }
}
