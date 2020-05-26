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
package cn.cedar.data.loader;

import cn.cedar.data.CedarDataBase;
import cn.cedar.data.expcetion.NotFoundDynamicMethodSqlException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cedar12.zxd@qq.com
 */
public class CedarDataLoader extends CedarDataBase {

	public static List<String> load(String path,Class<?> cls){
        if(!path.startsWith(FILE_SEPARATOR)){
            path =FILE_SEPARATOR + path;
        }
        if(!path.endsWith(FILE_SUFFIX)){
            path=path+FILE_SUFFIX;
        }
        if(!path.startsWith(FILE_SEPARATOR)){
            path=FILE_SEPARATOR+path;
        }
        List<String> list=new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(cls.getResourceAsStream(path)));
            String line = "";
            while (true) {
                try {
                    if (!((line = in.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list.add(line);
            }
        }catch (NullPointerException e){
            throw new NotFoundDynamicMethodSqlException(path);
        }
        return list;
    }
	
}
