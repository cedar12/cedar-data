/**
 *	  Copyright 2020 cedar12.zxd@qq.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.cedar.data;

import cn.cedar.data.struct.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cedar12.zxd@qq.com
 */
public class CedarDataBase extends HandlerConstant{

    protected static final String FILE_SEPARATOR="/";
    protected static final String FILE_SUFFIX=".cd";

    protected static final int ONE_TO_ONE=1;
    protected static final int ONE_TO_MANY=2;
    protected static final int MANY_TO_MANY=3;
    protected static final String ATTR_SPLIT=":";
    protected static final String ATTR_CLASS="class";
    protected static final String ATTR_ONE_TO_ONE="oo";
    protected static final String ATTR_ONE_TO_MANY="om";
    protected static final String ATTR_MANY_TO_MANY="mm";
    protected static final String START_CLASS="class:";
    protected static final String TYPE_SPLIT=",|\\|";
    protected static final String METHOD_SET="set";
    protected static final String METHOD_GET="get";
    protected static final String STR_NULL="null";

    protected static final String PATTERN_IMPORT="import(\\s.+)?(.+)?;";
    protected static final String PATTERN_DEF="def(\\s.+)?(.+)?=((.+));";
    protected static final String PATTERN_RELOLVER="#\\{(.+?)?\\}";
    protected static final String PATTERN_ANNO="(\\/\\*.*?\\*\\/)";
    protected static final String PATTERN_SPACE="(?!(\r|\n)).?";
    protected static final String PATTERN_BLOCK="(.+)?:(\\s*?)?\\{(.+?)?\\}(\\s*?)?;";
    protected static final String PATTERN_BLOCK_BEGIN="(.+?)?:(\\s*?)?\\{";
    protected static final String PATTERN_BLOCK_END="\\}(\\s*?)?;";
    protected static final String PATTERN_PRIVATE="private\\s+?";

    protected static final String CONTENT_BR="\r\n";
    protected static final String CONTENT_EMPTY="";
    protected static final String CONTENT_EMPTY_ONE=" ";
    protected static final String CONTENT_EMPTY_FIVE="     ";
    protected static final String CONTENT_COMMA=",";
    protected static final String CONTENT_MARK="!";


    protected static final String KEYWORD_IMPORT="import";
    protected static final String KEYWORD_DEF="def";
    protected static final String KEYWORD_PRIVATE="private";
    protected static final String KEYWORD_ANNO="//";

    protected static final String KEYWORD_TARGET="target";

    protected static int startRow=-1;
    protected List<String> contents;

    protected List<String> imports=new ArrayList<>();
    protected Map<String,String> defs=new HashMap<>();
    protected List<Block> blocks=new ArrayList<>();

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public Map<String, String> getDefs() {
        return defs;
    }

    public void setDefs(Map<String, String> defs) {
        this.defs = defs;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }
}
