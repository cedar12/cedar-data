/**
 * Copyright 2020 cedar12.zxd@qq.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.cedar.data.loader;

import cn.cedar.data.CedarDataBase;
import cn.cedar.data.expcetion.NotFoundDynamicMethodSqlException;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cedar12.zxd@qq.com
 */
public class CedarDataLoader extends CedarDataBase {

    public static List<String> load(String path, Class<?> cls) {
        if (!path.startsWith(FILE_SEPARATOR)) {
            path = FILE_SEPARATOR + path;
        }
        if (!path.endsWith(FILE_SUFFIX)) {
            path = path + FILE_SUFFIX;
        }
        List<String> list = new ArrayList<>();

        URL url=cls.getResource(FILE_SPLIT_SYMBOL);
        File file=new File(url.getFile()+path.substring(1));

        try {
            //BufferedReader in = new BufferedReader(new InputStreamReader(cls.getResourceAsStream(path)));
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = EMPTY_SYMBOL;
            while (true) {
                try {
                    if (!((line = in.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list.add(line);
            }
        } catch (NullPointerException e) {
            throw new NotFoundDynamicMethodSqlException(path);
        } catch (FileNotFoundException e) {
            throw new NotFoundDynamicMethodSqlException(path);
        }
        return list;
    }

}
