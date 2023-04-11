package com.chat.application.util;

public class JsScriptUtil {

    public static String codeTransfer(String content){
        String textContent = content;
        if (textContent.startsWith("html") || textContent.startsWith("<!DOCTYPE html>")){
            textContent = textContent
                    .replaceAll("<","&lt;")
                    .replaceAll(">","&gt;");
        }
        return textContent;
    }
    public static String getCodeContentScript(String textContent){
        String backgroundColor = "#C0C0C0";
        String content = "<pre style='background-color:"+backgroundColor+"'>" +
                "<code style='background-color:"+backgroundColor+"'>" + textContent + "</code></pre>";
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