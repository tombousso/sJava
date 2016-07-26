set -e
first=$1
shift
make $first
java -cp "bin:bin/$first:lib/*" Main "$@"
