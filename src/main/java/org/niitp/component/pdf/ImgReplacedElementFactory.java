package org.niitp.component.pdf;

import org.niitp.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

@Component
public class ImgReplacedElementFactory implements ReplacedElementFactory {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
        Element el = box.getElement();
        if (el == null) {
            return null;
        }
        String nodeName = el.getNodeName();
        if (nodeName.equals("img")) {
            String attribute = el.getAttribute("src");
            FSImage fsImage;
            final ClassPathResource regular = new ClassPathResource(attribute);
            try {
                fsImage = uac.getImageResource(regular.getURL().toString()).getImage();
            } catch (IOException e) {
                log.error("Картинка не найдена ", e);
                throw new AppException("Картинка не найдена " + e);
            }
            if (fsImage != null) {
                if (cssWidth != -1 || cssHeight != -1)
                    fsImage.scale(cssWidth, cssHeight);
                return new ITextImageElement(fsImage);
            }
        }
        return null;
    }

    public void remove(Element e) {
    }

    public void reset() {
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
    }
}
