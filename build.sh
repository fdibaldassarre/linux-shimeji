#!/bin/bash
set -e

# builds the current version of linux-shimeji
BASE_FOLDER="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
cd "$BASE_FOLDER"
mvn clean install


# Build the deb package
BASE="$BASE_FOLDER/target/deb_build"

## Create folders

mkdir -p "$BASE/usr/share/linux-shimeji"
mkdir -p "$BASE/usr/share/linux-shimeji/icons"

mkdir -p "$BASE/usr/bin"
mkdir -p "$BASE/usr/share/doc/linux-shimeji"
mkdir -p "$BASE/usr/share/man/man1"
mkdir -p "$BASE/usr/share/applications"

mkdir -p "$BASE/DEBIAN"

## Copy the files

# 1 - Icons
cp "src/main/resources/shime/img/icon.png" "$BASE/usr/share/linux-shimeji/icons/icon.png"

# 2 - Executables
cp "target/linux-shimeji-0.0.1-SNAPSHOT-jar-with-dependencies.jar" "$BASE/usr/share/linux-shimeji/shimeji.jar"
cp "launch.sh" "$BASE/usr/bin/linux-shimeji"

# 3 - Debian files (changelog/copyright/manual)
cp "DEBIAN/changelog" "$BASE/usr/share/doc/linux-shimeji/changelog.Debian"
cp "DEBIAN/copyright" "$BASE/usr/share/doc/linux-shimeji/copyright"
cp "DEBIAN/linux-shimeji.1" "$BASE/usr/share/man/man1/linux-shimeji.1"

# 4 - Desktop file
desktop_file="$BASE/usr/share/applications/linux-shimeji.desktop"
if [ -e "$desktop_file" ]; then
  rm "$desktop_file"
fi

cp "DEBIAN/linux-shimeji.desktop" "$desktop_file"

# get $VERSION
cd "$BASE/usr/share/doc/linux-shimeji/"
line=$( grep -E -m 1 -e "linux-shimeji\s(.*)" changelog.Debian )
line=${line//linux-shimeji (}
VERSION=${line//) *}

echo "Building Linux Shimeji $VERSION"

# 2 - compress

cd "$BASE/usr/share/doc/linux-shimeji"

# delete old changelogs archives

if [ -e changelog.gz ]; then
  rm changelog.gz
fi
if [ -e changelog.Debian.gz ]; then
  rm changelog.Debian.gz
fi

# create new changelogs .gz
gzip -n --best changelog.Debian

# compress the man
cd "$BASE/usr/share/man/man1"

# delete old man
if [ -e linux-shimeji.1.gz ]; then
  rm linux-shimeji.1.gz
fi

gzip -n --best linux-shimeji.1



## BASE PERMISSIONS

cd "$BASE"

# changelogs

cd "usr/share/doc/linux-shimeji"
chmod 644 *

cd "$BASE"

chmod 644 "$desktop_file"

cd "$BASE"

# program
cd "usr/share/linux-shimeji"

chmod 755 *.jar

# folders
chmod 755 icons/

# icons
cd icons/
chmod 644 *
cd ../

# CREATE CHECKSUM

checksum="$BASE/DEBIAN/md5sums"
if [ -e "$checksum" ]; then
  rm "$checksum"
fi
touch "$checksum"

cd "$BASE"
find "usr" -type f -print0 | xargs -0 md5sum >> "$checksum"

# CREATE CONTROL FILE

cd "$BASE/DEBIAN"

echo "Package: linux-shimeji" > control
echo "Version: $VERSION" >> control
echo "Section: web" >> control
echo "Priority: optional" >> control
echo "Architecture: all" >> control
echo "Depends: default-jre, jarwrapper" >> control
echo "Installed-Size: 3584" >> control
echo "Maintainer: Francesco Di Baldassarre <f.dibaldassarre@gmail.com>" >> control
echo "Provides: linux-shimeji" >> control
echo "Description: Linux version of the popular desktop mascot program, Shimeji." >> control
echo " This port supports both English and Japanese Shimeji." >> control

# BUILD
cd "$BASE_FOLDER/target"

fakeroot dpkg-deb --build deb_build/ linux-shimeji_"$VERSION"_all.deb

exit 0
