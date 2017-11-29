# AckBar
AckBar is simple, lightweight and customizable Android library to display in-app notifications.

* Show a message on top of the screen for a fixed period of time or make it sticky
* Queue message
* Add action to your message
* Add an additional message body
* Customize the message color

# How to
create your AckBar object:
```java
final AckBar ackBar = AckBar.make(MainActivity.this, R.string.test_1, android.R.color.black, 5000);
```
@optional
set a message body
```java
ackBar.setMsg(R.string.test_1_msg);
```

@optional
set an action
```java
 ackBar.setAction(new Runnable() {
  @Override
  public void run() {
     Toast.makeText(MainActivity.this, "Action", Toast.LENGTH_LONG).show();
  }
 });
 ```
 show
 ```java
 ackBar.show();
 ```
 dismiss
 ```java
 ackBar.dismiss();
 ```

# Download

add the repo on your root build.gradle file

```groovy
allprojects {
    repositories {
         maven {
            url "https://dl.bintray.com/murielk/maven/"
        }
    }
}
```

now add  the dependency on your project build.gradle file
```groovy
compile 'com.murielkamgang:AckBar:1.0.0'
```
# Screenshots
![GIF](gif/g_1.gif)
![Logo](screenshots/s_1.png)
![Logo](screenshots/s_3.png)
![Logo](screenshots/s_4.png)

#Copyright

Licensed under the GNU General Public License v2.0

* [GPLv2](LICENSE.md)

