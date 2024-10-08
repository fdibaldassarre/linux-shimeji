
# Shimeji for Linux

This is a fork of [Shimeji for Linux by asdfman](https://github.com/asdfman/linux-shimeji).

Not in active development.

# Installation

Download and install the latest .deb from the [Releases page](https://github.com/fdibaldassarre/linux-shimeji/releases).

To run open the Shimeji application or execute `linux-shimeji` from the terminal.

## Alternative install with Distrobox

To install the application using [Distrobox](https://distrobox.it/).
```commandline
# Create container
distrobox create --name shimeji-test -i ubuntu:20.04 --additional-packages openjdk-11-jre
# Enter the container
distrobox enter shimeji-test
# Inside the container install the package
cd /path/to/shimeji/deb
sudo dpkg -i linux-shimeji_*_all_nodep.deb
```

You can the run the Shimeji application with `distrobox enter shimeji-test -- linux-shimeji`.

# Installing new shimeji
To install a new shimeji copy the shimeji folder in`~/.config/shimeji`.

Then edit the file `~/.config/shimeji/config.ini` with the name of the folder
```
[shimeji]
name = new_shimeji
language = ENG
```

Set language to ENG if the Shimeji xml uses english tags, JPN if it uses japanese tags.

To use the default shimeji set the configuration file to
```
[shimeji]
name = shimeji
language = JPN
```

### Window configuration

It is possible to adjust the window position using the file `~/config/window.conf`.
The file should contain four values, one for each line:
- X-axis offset
- Y-axis offset
- window width delta
- window height delta

For instance the file

```
10
-20
100
200
```

Will cause the windows top-right corner to be moved 10px to the right and 20px to the top.
The windows width will be increased by 100px and the height by 200px.

# Development

## Requirements

- Java 11 (openjdk-11-jre)


## Building
To build the project run `mvn clean install`

## Running
Execute `./launch.sh`.



## Building the package
Run `./build.sh` to build a Debian/Ubuntu package.

The deb will install a Shimeji application under 'Accessory'.

To run from terminal use `shimeji-linux`.


# License
This project inherits the ZLIB/LIBPNG license of the original [program](http://www.group-finity.com/Shimeji). 
The included [Java Native Access](http://github.com/twall/jna) library is licensed under the LGPL. [The Mozilla Rhino Javascript Engine](http://www.mozilla.org/rhino)
is licensed under the Mozilla Public License.


# Todo
- Port to Java 17
- Support BornMascot parameter


