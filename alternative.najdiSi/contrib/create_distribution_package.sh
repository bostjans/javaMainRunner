#!/bin/sh

# $Id: create_distribution_package.sh 17360 2008-06-12 13:02:09Z bfg $
# $URL: https://svn.interseek.com/repositories/modules/javaapp/trunk/contrib/create_distribution_package.sh $
# $Date: 2008-06-12 15:02:09 +0200 (Thu, 12 Jun 2008) $
# $Author: bfg $
# $Revision: 17360 $
# $LastChangedRevision: 17360 $
# $LastChangedBy: bfg $
# $LastChangedDate: 2008-06-12 15:02:09 +0200 (Thu, 12 Jun 2008) $

#################################################
#                  FUNCTIONS                    #
#################################################
MYNAME="`basename $0`"
VERSION="0.10"
PATH="${PATH}:/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin"
export PATH

MVN="`which mvn`"
PRODUCTION=""
VERBOSE="0"
DIST_DIR="/tmp"

tty_colors_init() {
	# stdout and stderr *must* be
	# tty in order to install real shell
	# color codes...
	if [ -t 1 -a -t 2 ]; then
		TERM_WHITE="\033[1;37m"
		TERM_YELLOW="\033[1;33m"
		TERM_LPURPLE="\033[1;35m"
		TERM_LRED="\033[1;31m"
		TERM_LCYAN="\033[1;36m"
		TERM_LGREEN="\033[1;32m"
		TERM_LBLUE="\033[1;34m"
		TERM_DGRAY="\033[1;30m"
		TERM_GRAY="\033[0;37m"
		TERM_BROWN="\033[0;33m"
		TERM_PURPLE="\033[0;35m"
		TERM_RED="\033[0;31m"
		TERM_CYAN="\033[0;36m"
		TERM_GREEN="\033[0;32m"
		TERM_BLUE="\033[0;34m"
		TERM_BLACK="\033[0;30m"
		TERM_BOLD="\033[40m\033[1;37m"
		TERM_RESET="\033[0m"
	else
		TERM_WHITE=""
		TERM_YELLOW=""
		TERM_LPURPLE=""
		TERM_LRED=""
		TERM_LCYAN=""
		TERM_LGREEN=""
		TERM_LBLUE=""
		TERM_DGRAY=""
		TERM_GRAY=""
		TERM_BROWN=""
		TERM_PURPLE=""
		TERM_RED=""
		TERM_CYAN=""
		TERM_GREEN=""
		TERM_BLUE=""
		TERM_BLACK=""
		TERM_BOLD=""
		TERM_RESET=""
	fi
}

die() {
	echo -e "${TERM_LRED}FATAL ERROR${TERM_RESET}: $@"
	echo -e "Run $MYNAME -h for instructions."
	exit 1
}

msg_warn() {
	echo -e "${TERM_YELLOW}WARNING: ${TERM_RESET}$@"
}

msg_info() {
	echo -e "${TERM_BOLD}INFO:    ${TERM_RESET}$@"
}

msg_fatal() {
	echo -e "${TERM_LRED}FATAL:   ${TERM_RESET}$@"
	exit 1
}

printhelp() {
	echo -e "${TERM_BOLD}Usage:${TERM_RESET} ${TERM_LGREEN}$MYNAME${TERM_RESET} [OPTIONS]<PRODUCTION>"
	echo ""
	echo "This script builds tar.bz2 bundle of all required jars."
	echo "Bundle is ready for deployment to production."
	echo ""
	echo "This script requires:"
	echo ""
	echo "	- Maven >= 2.0.4 (http://maven.apache.org/)"
	echo "		- configured global maven2 settings (\${M2_HOME}/conf/settings.xml)"
	echo "		https://svn.interseek.com/repositories/modules/maven/apache-maven-2.0.9/conf/settings.xml"
	echo ""
	echo "		- configured local maven2 settings (~/.m2/settings.xml)"
	echo "		https://svn.interseek.com/repositories/modules/maven/settings.xml"
	echo ""
	echo "	- Java SDK >= 1.5.0 (http://java.sun.com/)"
	echo "		- java VM must include Interseek ROOT CA certificate in it's certificate keystore"
	echo "		- Certificate URL: https://ca.interseek.com/interseek-ca.pem"
	echo "		- HOWTO: http://intra.noviforum.si/dokumentacija/prirocniki/admin/certification_authority/ca_usage"
	echo ""
	echo "	- Subversion client >= 1.4.0"
	echo ""
	echo "	- tar and bzip2 command line utilities" 
	echo ""
	echo "	- configured \${JAVA_HOME} variable";
	echo ""
	echo -e "${TERM_BOLD}OPTIONS:${TERM_RESET}"
	echo "  -D           Directory for dropping jar bundles (Default: \"${DIST_DIR}\")"
	echo ""
	echo "  -v           Verbose execution"
	echo "  -V           Prints out script version"
	echo "  -h           This help message"
}

script_init() {
	local f=""
	# jvm stuff...
	test -z "${JAVA_HOME}" && die "Undefined variable \${JAVA_HOME}"
	test -d "${JAVA_HOME}" -a -r "${JAVA_HOME}" || die "Invalid \${JAVA_HOME}: ${JAVA_HOME}"
	
	# check for compilers
	f="${JAVA_HOME}/bin/javac"
	test -f "$f" -a -x "$f" || die "Unable to find javac in \${JAVA_HOME}"
	f="${JAVA_HOME}/bin/rmic"
	test -f "$f" -a -x "$f" || die "Unable to find rmic in \${JAVA_HOME}"

	# maven stuff...
	test -z "${MVN}" && die "Unable to find maven binary mvn in \$PATH"
	test -f "${MVN}" -a -x "${MVN}" || die "Invalid maven binary: $MVN"
	f=~/.m2/settings.xml
 	test -f "$f" -a -r "$f" || die "Invalid local maven configuration file: $f"
 	
 	# required vars
 	test -z "${PRODUCTION}" && die "Undefined production name."
 	
 	# destination directory...
 	test -d "${DIST_DIR}" -a -w "${DIST_DIR}" || die "Invalid or unwriteable DIST_DIR: ${DIST_DIR}"

	return 0
}

run_it() {
	msg_info "Cleaning maven project."
	if [ "${VERBOSE}" = "1" ]; then
		${MVN} clean || die "Unable to clean maven project."
	else
		${MVN} clean >/dev/null 2>&1 || die "Unable to clean maven project."
	fi
	
	msg_info "Creating project assembly."
	if [ "${VERBOSE}" = "1" ]; then
		${MVN} -Dproduction=${PRODUCTION} assembly:assembly || die "Unable to create project assembly."
	else
		${MVN} -Dproduction=${PRODUCTION} assembly:assembly >/dev/null 2>&1 || die "Unable to create project assembly."
	fi
	
	msg_info "Creating METADATA file."
	local pwd="`pwd`"
	local f="000-METADATA.TXT"

	cd target/*-deploy.dir || die "Unable to enter target directory."
	
	echo "############################################" >> "$f"
	echo "#            PACKAGE METADATA              #" >> "$f"
	echo "############################################" >> "$f"
	echo "" >> "$f"
	printf "# Created by:   %s %-2.2f\n" $MYNAME $VERSION >> "$f"
	echo "# Created on:  " `date +"%Y/%m/%d at %H:%M:%S"` >> "$f"
	echo "" >> "$f"
	echo "############################################" >> "$f"
	echo "#             FILE CHECKSUMS               #" >> "$f"
	echo "############################################" >> "$f"
	echo "" >> "$f"
	md5sum * | grep -v "$f" >> "$f"

	echo "" >> "$f"
	echo "# EOF" >> "$f"

	msg_info "Creating tar.bz2 archive."
	f="${DIST_DIR}/jar-bundle-${PRODUCTION}-`date +%Y%m%d%H%M%S`.tbz"
	tar cjpf "${f}" * || die "Unable to create tar.bz2 archive."
	
	# create tbz checksum...
	md5sum "${f}" | awk '{print $1}' > "${f}.md5"
	
	msg_info "Performing cleanup."
	cd "${pwd}"
	${MVN} clean >/dev/null 2>&1
	
	msg_info "Operation complete. Bundle file written to: ${TERM_LGREEN}${f}${TERM_RESET}"
	return 0
}

#################################################
#                    MAIN                       #
#################################################

# initialize shell colours...
tty_colors_init

# parse command line
while getopts "D:vVh" opt; do
	case $opt in
		D)
			DIST_DIR="${OPTARG}"
			;;
		v)
			VERBOSE=1
			;;
		V)
			printf "%s %-2.2f\n" "${MYNAME}" "${VERSION}"
			exit 0
			;;
		h)
			printhelp
			exit 0
			;;
	esac
done

# normalize command line...
i=1
while [ $i -lt $OPTIND ]; do
        shift;
        i=$((i + 1))
done
unset i

# fetch production name...
PRODUCTION="${1}"

# initialize script
script_init || die "Unable initialize script..."

# run the operation
run_it

# EOF