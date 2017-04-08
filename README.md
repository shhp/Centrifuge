# When to use
Launch performance is one of the key issues in developing an Android app. If several developers are collaboratively developing an Android app, anyone is likely to push a commit which influence the launch performance unintentionally. For example, Bob just added a statement in the method `whatever` of class `SomeClass`. However, `whatever` is invoked in `onCreate` of the `MainActivity`, thus such change may influence the launch performance. Bob just pushed the commit without realizing the underlying influence. So the problem is how can the team memebers know whether a commit may influence the launch performance or not.

**Centrifuge** is aimed to solve the problem. It utilizes **java annotation** and **annotation processor tool** to extract code snippets which you are concerned about into a single file. By adding that file to git, you can track all the changes within the important code snippets.

# How to use
1. Add these dependencies to your app's `build.gradle`:

  ```
  compile 'com.shhp.centrifuge:centrifuge-annotation:2.0.2'
  annotationProcessor 'com.shhp.centrifuge:centrifuge-annotation-processor:2.0.2'
  ```
  
2. Use the annotation `CodeExtractor` to annotate your custom annotations. e.g.

  ```
  @Documented
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
  @CodeExtractor
  public @interface Core {
  }
  ```
  There is already a predefined annotation `Centrifuge` annotated with `@CodeExtractor`.

3. Use your custom annotations or `Centrifuge` to annotate `class`, `constructor` or `method` you are concerned about. When these annotations are used to annotate `class`, all the static blocks within that `class` will be extracted. And when they are used to annotate `constructor` or `method`, the method body will be extracted.

4. Build your project and all the code snippets will be extracted into a file for each annotation located in `{module}/build/generated/source/apt/{productFlavor}/{buildType}/centrifuge/{annotation name}`. There may be several items in each file, each item is in the form of

  ```
  // {identifier}
  {content}
  ```
  
  When the item is associated with a `class`, the `identifier` is the full name of the class including package name. When the item is associated with a `method`, the `identifier` is in the form of *{full name of the class}#{method name}({parameters})*. When the item is associated with a `constructor`, the `identifier` is in the form of *{full name of the class}#&lt;init&gt;({parameters})*. 
  
5. Include the generated files in version control by adding these lines to your project's `.gitignore`:

  ```
  /app/build/*
!/app/build/generated/
/app/build/generated/*
!/app/build/generated/source/
/app/build/generated/source/*
!/app/build/generated/source/apt/
/app/build/generated/source/apt/*
!/app/build/generated/source/apt/debug/
/app/build/generated/source/apt/debug/*
!/app/build/generated/source/apt/debug/Centrifuge/
  ```
  
#Example

Define an annotation:

```
@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
@CodeExtractor
public @interface Core {
}
```

Use annotations

SampleActivity.java:

```
@Centrifuge
public class SampleActivity extends AppCompatActivity {

    static {
        Log.i("Test", "This is a static block.");
    }

    @Override
    @Centrifuge
    @Core
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
    }

}
```

Test.java:

```
public class Test {

    @Core
    private void test() {
        Log.i("test", "just a test");
    }
}
```

Two files will be generated after building the project.

Centrifuge:

```
// com.shhp.centrifuge.SampleActivity
static {
    Log.i("Test", "This is a static block.");
}



// com.shhp.centrifuge.SampleActivity#onCreate(android.os.Bundle savedInstanceState,)
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sample);
}

```

Core:

```
// com.shhp.centrifuge.SampleActivity#onCreate(android.os.Bundle savedInstanceState,)
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sample);
}

// com.shhp.centrifuge.Test#test()
{
    Log.i("test", "just a test");
}


```


#License

```
Copyright 2017 shhp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
