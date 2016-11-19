currentdir=$( cd $(dirname $0); pwd -P )

jjs -scripting ${currentdir}/geec.js --  "$*"
