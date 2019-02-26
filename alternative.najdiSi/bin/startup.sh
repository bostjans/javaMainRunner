#!/bin/sh

# $Id: startup.sh 19001 2008-08-06 22:33:52Z bfg $
# $URL: https://svn.interseek.com/repositories/modules/javaapp/trunk/bin/startup.sh $
# $Date: 2008-08-07 00:33:52 +0200 (Thu, 07 Aug 2008) $
# $Author: bfg $
# $Revision: 19001 $
# $LastChangedRevision: 19001 $
# $LastChangedBy: bfg $
# $LastChangedDate: 2008-08-07 00:33:52 +0200 (Thu, 07 Aug 2008) $

#############################################
#                 GLOBALS                   #
#############################################

JVM_OPT="-server -Xmx512M"
J_CLASS="com.company.mypackage.MyBootClass"

#############################################
#                 FUNCTIONS                 #
#############################################
BASEDIR=""
JVM=""

die() {
	echo "ERROR: $@"
	exit 1
}

basedir_get() {
	local bin=""
	# echo "\$0 = $0"
	local dir="`dirname $0`"
	
	# are we symlinked?
	if [ -L "${0}" ]; then
		local real_bin=`readlink "${0}"`
		test -z "${real_bin}" && die "Unable to determine BASEDIR: '$0' => '${bin}' seems to be a symlink pointing to nowhere."
		bin="${real_bin}"
	else
		bin="${0}"
	fi
	
	# basedir is one level higher than bin...
	# echo "BIN: $bin"
	BASEDIR=`dirname "${bin}"`
	if [ "${BASEDIR}" == "." ]; then
		BASEDIR=`pwd`
	fi
	# echo "BASEDIR: $BASEDIR"
	if [ "${0:0:2}" != "./" -o "${0:0:2}" != ".." ]; then
		BASEDIR=`dirname "${BASEDIR}"`
	fi
	test "${BASEDIR}" = "/" && die "Unable to determine BASEDIR: multi-level binary symlinks."

	#echo "BASEDIR: $BASEDIR"
}

classpath_set() {
	local dir="${1}"
	test -z "${dir}" && dir="${BASEDIR}"
	local f=""
	test -d "${dir}" -a -r "${dir}" || die "Invalid base directory: ${dir}"
	CLASSPATH=""
	local libdir="${dir}/lib"
	test -d "${libdir}" -a -r "${libdir}" || die "Invalid lib directory: ${libdir}"
	for f in `find "${libdir}" -name "*.jar"`; do
		CLASSPATH="${CLASSPATH}:$f"
	done
	CLASSPATH="${CLASSPATH}:${dir}/bin:${dir}/target-eclipse/classes"
	export CLASSPATH
}

# try to figure out JVM
jvm_get() {
	local latest_jvm="/export/software/java/`ls /export/software/java 2>/dev/null| tail -n 1`"
	local jvm_dir=""
	local jvm_bin=""

	for d in "${JAVA_HOME}" ${latest_jvm} "usr" "/usr/local"; do
		test -z "$d" && continue
		test -z "${jvm_dir}" || break
		for f in "java" "jre" "java.exe" "jre.exe"; do
			local ex="${d}/bin/${f}"
			if [ -x "${ex}" -a -f "${ex}" ]; then
				#JVM="${ex}"
				#JAVA_HOME=`dirname "${JVM}"`
				#JAVA_HOME=`dirname "${JAVA_HOME}"`
				jvm_dir="$d"
				jvm_bin="$ex"
				break
			fi
		done
	done

	test -z "${jvm_bin}" && die "Unable to find JavaVM. Set \${JAVA_HOME} env key."

	JAVA_HOME="$jvm_dir"
	JVM="$jvm_bin"
	export JAVA_HOME
}

#############################################
#                  MAIN                     #
#############################################

# determine libdir
basedir_get
# echo "BASEDIR=${BASEDIR}"

# set the classpath
classpath_set "$BASEDIR"
# echo "CLASSPATH: $CLASSPATH"

# get jvm...
jvm_get
# echo "JVM: $JVM, JAVA_HOME: $JAVA_HOME"

# run the bastard
exec ${JVM} ${JVM_OPT} ${J_CLASS} $@

# EOF
