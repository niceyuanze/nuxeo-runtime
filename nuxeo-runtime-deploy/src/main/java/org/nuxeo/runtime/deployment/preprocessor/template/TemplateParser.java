/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.runtime.deployment.preprocessor.template;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.nuxeo.common.utils.FileUtils;

/**
 * @author  <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class TemplateParser {

    // Utility class.
    private TemplateParser() {
    }

    public static Template parse(File file) throws IOException {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            return parse(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static Template parse(URL url) throws IOException {
        InputStream in = null;
        try {
            in = new BufferedInputStream(url.openStream());
            return parse(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static Template parse(InputStream in) throws IOException {
        String s = FileUtils.read(in);
        return parse(s.toCharArray());
    }

    public static Template parse(char[] chars) {
        Template tpl = new Template();
        StringBuilder buf = new StringBuilder();
        StringBuilder name = new StringBuilder();

        // add the begin part
        tpl.addPart(Template.BEGIN, null);

        boolean marker = false;
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            switch (ch) {
            case '%':
                if (i < chars.length && chars[i + 1] == '{') {
                    marker = true;
                    i++;
                } else {
                    if (marker) {
                        name.append(ch);
                    } else {
                        buf.append(ch);
                    }
                }
                break;
            case '}':
                if (i < chars.length && chars[i + 1] == '%') {
                    marker = false;
                    i++;
                    // create  a new Part:
                    tpl.addPart(name.toString(), buf.toString());
                    name.setLength(0);
                    buf.setLength(0);
                } else {
                    if (marker) {
                        name.append(ch);
                    } else {
                        buf.append(ch);
                    }
                }
                break;
            default:
                if (marker) {
                    name.append(ch);
                } else {
                    buf.append(ch);
                }
                break;
            }
        }

        // create the END part
        tpl.addPart(Template.END, buf.toString());

        return tpl;
    }

}
