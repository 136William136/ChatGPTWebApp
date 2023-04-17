package com.chat.application.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsScriptUtil {

    public static String codeTransfer(String content){
        String pattern = "(.*?)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        if (m.find()){
            return content
                    .replaceAll("<","&lt;")
                    .replaceAll(">","&gt;");
        }
        return content;
    }
    public static String getCodeContentScript(String textContent){
        String backgroundColor = "#2C3E50";
        String content = "<pre style='color:#F7F9F9;background-color:"+backgroundColor+"'>" +
                "<code style='color:#F7F9F9;background-color:"+backgroundColor+"'>" + textContent + "</code></pre>";
        return content;
    }

    public static String copyContentScript(){
        String context = "if (navigator.clipboard && window.isSecureContext) {\n" +
                "                return navigator.clipboard.writeText($0);\n" +
                "            } else {\n" +
                "                let textArea = document.createElement(\"textarea\");\n" +
                "                textArea.value = $0;\n" +
                "                textArea.style.position = \"absolute\";\n" +
                "                textArea.style.opacity = 0;\n" +
                "                textArea.style.left = \"-999999px\";\n" +
                "                textArea.style.top = \"-999999px\";\n" +
                "                document.body.appendChild(textArea);\n" +
                "                textArea.focus();\n" +
                "                textArea.select();\n" +
                "                return new Promise((res, rej) => {\n" +
                "                    document.execCommand('copy') ? res() : rej();\n" +
                "                    textArea.remove();\n" +
                "                });\n" +
                "            }";
        return context;
    }

}
