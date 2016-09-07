<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="text" omit-xml-declaration="yes"/>
	<!--**************************************************************-->
	<!--library_patron.xsl: tranforms registry xml into               -->
	<!-- loadflatuser format                                          -->
	<!--  updated 04/17/2012 for new name format and webauth id - jkg -->
	<!-- use: no parameters                                           -->
	<!-- 011509 ajm created for uni-4                                 -->
	<!--**************************************************************-->
	<!--**************************************************************-->
	<!--Global Variables -->
	<!--**************************************************************-->
	<xsl:variable name="SPACER" select="'   |a'" />

	<!--**********************************************************-->
	<!--Person - Decides if we should process this person -->
	<!--**********************************************************-->
	<xsl:template match="Person">
		<!-- only process this person if they have a library card id.-->
		<xsl:choose>
			<xsl:when test="@card">
				<xsl:call-template name="main"/>
			</xsl:when>
			<xsl:when test="identifier[@type='card']">
				<xsl:call-template name="main"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<!--**********************************************************-->
	<!--Main - The main template that drives everything else -->
	<!--**********************************************************-->
	<xsl:template name="main">
		<!--****************-->
		<!-- ID - selects the univid  -->
		<!--****************-->
		<xsl:variable name="USER_ALT_ID">
			<xsl:choose>
				<xsl:when test="@univid">
					<xsl:value-of select="@univid"/>
				</xsl:when>
				<xsl:when test="identifier[@type='refid' and substring(@nval,1,10)='campuscard']">
					<xsl:value-of select="substring(identifier[@type='refid'],12)"/>
				</xsl:when>
				<xsl:when test="identifier[@type='refid' and substring(@nval,1,8)='campcomm']">
					<xsl:value-of select="substring(identifier[@type='refid'],10)"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<!--****************-->
		<!-- CARD NUMBER - mag stripe number  -->
		<!--****************-->
		<xsl:variable name="USER_ID">
			<xsl:choose>
				<xsl:when test="identifier[@type='card']">
					<xsl:value-of select="substring(identifier[@type='card'] [last()],6)"/>
				</xsl:when>
				<xsl:when test="@card">
					<xsl:value-of select="substring(@card,6)"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<!--****************-->
		<!-- Name - the registered name which is always present. Use display name first, otherwise use name type that takes up three columns. The format is first name, middle initial, first name,-->
		<!--****************-->
		<xsl:variable name="USER_NAME">
			<xsl:choose>
				<xsl:when test="name[@type='registered']">
					<xsl:value-of select="name/last"/>
					<xsl:text>, </xsl:text>
					<xsl:value-of select="name/first"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="name/middle"/>
				</xsl:when>
				<xsl:when test="@name">
					<xsl:value-of select="@name"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="USER_FIRST_NAME">
			<xsl:choose>
				<xsl:when test="name[@type='registered']">
					<xsl:value-of select="name/first"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="USER_MIDDLE_NAME">
			<xsl:choose>
				<xsl:when test="name[@type='registered']">
					<xsl:value-of select="name/middle"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="USER_LAST_NAME">
			<xsl:choose>
				<xsl:when test="name[@type='registered']">
					<xsl:value-of select="name/last"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<!--****************-->
		<!-- Primary contact address consists of: the first line of the address element, the city, state, and zip code.-->
		<!--****************-->
		<xsl:variable name="USER_ADDR1">
			<xsl:choose>
				<xsl:when test="@relationship='student' and address[@type='homemail']">
					<xsl:apply-templates select="address[@type='homemail']"/>
				</xsl:when>
				<xsl:when test="@relationship='student' and address[@type='mail']">
					<xsl:apply-templates select="address[@type='mail']"/>
				</xsl:when>
				<xsl:when test="@relationship='student' and address[@type='local']">
					<xsl:apply-templates select="address[@type='local']"/>
				</xsl:when>
				<xsl:when test="@relationship='staff' and place/address[@type='workmail']">
					<xsl:apply-templates select="place/address[@type='workmail']"/>
				</xsl:when>
				<xsl:when test="@relationship='staff' and place/address[@type='work']">
					<xsl:apply-templates select="place/address[@type='work']"/>
				</xsl:when>
				<xsl:when test="@relationship='faculty' and address[@type='mail']">
					<xsl:apply-templates select="address[@type='mail']"/>
				</xsl:when>
				<xsl:when test="@relationship='faculty' and address[@type='work']">
					<xsl:apply-templates select="address[@type='work']"/>
				</xsl:when>
				<xsl:when test="@relationship='affiliate' and address[@type='mail']">
					<xsl:apply-templates select="address[@type='mail']"/>
				</xsl:when>
				<xsl:when test="address[@type='general']">
					<xsl:apply-templates select="address[@type='general']"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<!--****************-->
		<!-- Email - the first email address on the list-->
		<!--****************-->
		<xsl:variable name="EMAIL">
			<xsl:choose>
				<xsl:when test="email">
					<xsl:value-of select="substring(concat(email/user, '@', email/host), 1, 50)"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<!--****************-->
		<!-- Telephone - the work telephone number.  -->
		<!-- Telephone consists of: <area code>-<telephone number> -->
		<!--****************-->
		<xsl:variable name="PHONE">
			<xsl:choose>
				<xsl:when test="@relationship='staff' and place/telephone[@type='work']">
					<xsl:text>(</xsl:text>
					<xsl:value-of select="place/telephone[@type='work']/area"/>
					<xsl:text>) </xsl:text>
					<xsl:value-of select="place/telephone[@type='work']/number"/>
				</xsl:when>
				<xsl:when test="@relationship='faculty' and telephone[@type='local']">
					<xsl:text>(</xsl:text>
					<xsl:value-of select="telephone[@type='local']/area"/>
					<xsl:text>) </xsl:text>
					<xsl:value-of select="telephone[@type='local']/number"/>
				</xsl:when>
				<xsl:when test="@relationship='student' and place/telephone[@type='residence']">
					<xsl:text>(</xsl:text>
					<xsl:value-of select="place/telephone[@type='residence']/area"/>
					<xsl:text>) </xsl:text>
					<xsl:value-of select="place/telephone[@type='residence']/number"/>
				</xsl:when>
				<xsl:when test="@relationship='student' and place/telephone[@type='work']">
					<xsl:text>(</xsl:text>
					<xsl:value-of select="place/telephone[@type='work']/area"/>
					<xsl:text>) </xsl:text>
					<xsl:value-of select="place/telephone[@type='work']/number"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<!--****************-->
		<!-- Secondary address consists of: the first line of the home address element, the city, state, and zip code.-->
		<!--****************-->
		<xsl:variable name="USER_ADDR2">
			<xsl:choose>
				<xsl:when test="@relationship='student' and address[@type='work']">
					<xsl:apply-templates select="address[@type='work']"/>
				</xsl:when>
				<xsl:when test="address[@type='general']">
					<xsl:apply-templates select="address[@type='general']"/>
				</xsl:when>
				<xsl:when test="address[@type='permanent']">
					<xsl:apply-templates select="address[@type='permanent']"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<!--****************-->
		<!-- Secondary telephone - the work telephone number.  -->
		<!-- Telephone consists of: <area code>-<telephone number> -->
		<!--****************-->
		<xsl:variable name="PHONE2">
			<xsl:choose>
				<xsl:when test="telephone[@type='general'] or telephone[@type='permanent'] or telephone[@type='home']">
					<xsl:text>(</xsl:text>
					<xsl:value-of select="telephone/area"/>
					<xsl:text>) </xsl:text>
					<xsl:value-of select="telephone/number"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<!--****************-->
		<!-- LOCATION: department code  -->
		<!--****************-->
		<xsl:variable name="USER_DEPARTMENT">
			<xsl:apply-templates select="affiliation/department" />
		</xsl:variable>
		<!--****************-->
		<!-- Profile/privg -->
		<!--****************-->
		<xsl:variable name="USER_PROFILE">
			<xsl:choose>
				<xsl:when test="affiliation[position()=1 and (@type='staff:academic' or @type='staff:otherteaching' or @type='staff:emeritus')]">CNAC</xsl:when>
				<xsl:when test="affiliation[position()=1 and (@type='staff:casual' or @type='staff:parttime' or @type='staff:temporary' or @type='staff:temp')]">KEEP</xsl:when> <!-- uni-34 -->
				<xsl:when test="affiliation[position()=1 and @type='staff:student']">KEEP</xsl:when>
				<xsl:when test="affiliation[position()=1 and @type='staff:otherteaching']">CNAC</xsl:when>
				<!-- <xsl:when test="affiliation[position()=1 and @type='staff:onleave']">RJCT</xsl:when> -->
				<xsl:when test="affiliation[position()=1 and @type='staff:onleave']">KEEP</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,5)='staff']">CNS</xsl:when>
				<xsl:when test="affiliation[position()=1 and @type='faculty:otherteaching']">MXF</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,7)='faculty']">CNF</xsl:when>
				<xsl:when test="affiliation[position()=1 and @type='student:mla']">REG</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,7)='student'] and privgroup='student:phd'">RED</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,7)='student'] and privgroup='student:postdoc'">RED</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,7)='student'] and privgroup='student:doctoral'">RED</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,7)='student'] and affiliation/affdata='Law JSD'">RED</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,7)='student'] and affiliation/description='Graduate' and (affiliation/affdata[@affnum = '1' and @code='GRNM1'] or affiliation/affdata[@affnum = '1' and @code='GRNM3'])">REG-SUM</xsl:when> <!-- uni-34 -->
				<xsl:when test="affiliation[position()=1 and substring(@type,1,7)='student'] and substring(affiliation/description,1,8)='Graduate'">REG</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,7)='student'] and affiliation/description='Undergraduate' and (affiliation/affdata[@affnum = '1' and @code='UGNM1'] or affiliation/affdata[@affnum = '1' and @code='UGNM2'] or affiliation/affdata[@affnum = '1' and @code='UGNM3'])">REU-SUM</xsl:when> <!-- uni-34 -->
				<xsl:when test="affiliation[position()=1 and substring(@type,1,7)='student'] and affiliation/description='Undergraduate'">REU</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='LIBBO-SUL']">KEEP</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='LIBBO-GSB']">LIBRJCT</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='LIBBO-LANE']">LIBRJCT</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='LIBBO-LAW']">MXFEE</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='LIBBO']">KEEP</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='AF']">MXS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='BF']">MXF</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='BOT']">MXF</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='CARNEGIE']">MXS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='CF']">MXF</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='CS']">REG</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='FA']">CNF</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='HHMI']">MXS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='HS']">MXS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='MLA']">REG</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='PA']">MXS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='RFK']">MXAS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='VF']">MXF</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='CA']">MXAC</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='TA']">MXAC</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='VA']">MXAC</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='SFS']">MXAS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='SA']">CNAC</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='EFEL']">CNAC</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='VSCP']">MXAS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='SN']">CNS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='SO']">MXAC</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='STS']">MXAS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='VO']">MXAS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation/affdata[@code='VOS']">MXAS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation[position()=2 and @type='staff:retired']">CNS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation[position()=2 and @type='faculty:retired']">CNF</xsl:when>
				<xsl:when test="affiliation[position()=1 and @type='affiliate:courtesy']">MXAS</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate']">MXAS</xsl:when>
				<xsl:otherwise>
					<xsl:text>KEEP</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!--****************-->
		<!-- Today's date, will be the system date -->
		<!--****************-->
		<xsl:variable name="USER_PRIV_GRANTED">TODAYSDATE</xsl:variable>
		<!--****************-->
		<!-- Expiration date  -->
		<!--****************-->
		<xsl:variable name="USER_PRIV_EXPIRES">
			<xsl:choose>
				<xsl:when test="affiliation[position()=1 and @type='faculty:nonactive']">EXPIRED</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,7)='faculty']">NEVER</xsl:when>
				<xsl:when test="affiliation[position()=1 and @type='staff:nonactive']">EXPIRED</xsl:when>
				<!-- uni-29 <xsl:when test="affiliation[position()=1 and @type='staff:temporary']">EXPIRED</xsl:when> -->
				<xsl:when test="affiliation[position()=1 and substring(@type,1,5)='staff' and @type!='staff:student' and @type!='staff:casual']">NEVER</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation[position()=2 and @type='student:recent']">EXPIRED</xsl:when>
				<xsl:when test="affiliation[position()=1 and @type='student:recent']">EXPIRED</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation[position()=2 and @type='staff:retired']">NEVER</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation[position()=2 and @type='affiliate:sponsored']">KEEP</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate' and @type='affiliate:visitscholarvs']">KEEP</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate' and @type='affiliate:fellow']">NEVER</xsl:when>
				<xsl:when test="affiliation[position()=1 and substring(@type,1,9)='affiliate'] and affiliation[position()=2 and @type='affiliate:visitscholarvs']">KEEP</xsl:when>
				<xsl:when test="affiliation[position()=1]/@until">
					<xsl:value-of select="substring(affiliation/@until,1,4)"/>
					<xsl:value-of select="substring(affiliation/@until,6,2)"/>
					<xsl:value-of select="substring(affiliation/@until,9,2)"/>
				</xsl:when>
				<xsl:when test="@stanfordenddate">
					<xsl:value-of select="substring(@stanfordenddate,1,4)"/>
					<xsl:value-of select="substring(@stanfordenddate,6,2)"/>
					<xsl:value-of select="substring(@stanfordenddate,9,2)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>EXPIRED</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!--****************-->
		<!-- SUNETID - selects the sunetid  -->
		<!--****************-->
		<xsl:variable name="USER_GROUP_ID">
			<xsl:choose>
				<xsl:when test="@sunetid">
					<xsl:value-of select="@sunetid"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<!--****************-->
		<!-- DEPT - puts (e.g.) organization:gsp privgroup in the USER_ADDR2/DEPT field-->
		<!--****************-->
		<xsl:variable name="DEPT1">
			<xsl:apply-templates select="affiliation/department" />
		</xsl:variable>

		<xsl:variable name="DEPT2">
			<xsl:apply-templates select="privgroup"/>
		</xsl:variable>

		<!-- ***************************************************************************************************************************************************** -->
		<!-- Write to file -->
		<!-- ***************************************************************************************************************************************************** -->
		<xsl:text>.REC.</xsl:text><xsl:text>&#10;</xsl:text>
		<xsl:text>*** DOCUMENT BOUNDARY ***</xsl:text><xsl:text>&#10;</xsl:text>
		<xsl:text>FORM=LDUSER</xsl:text><xsl:text>&#10;</xsl:text>
		<xsl:value-of select="concat('.USER_ID.',$SPACER,$USER_ID)"/><xsl:text>&#10;</xsl:text>
		<xsl:value-of select="concat('.USER_ALT_ID.',$SPACER,$USER_ALT_ID)"/><xsl:text>&#10;</xsl:text>
		<xsl:value-of select="concat('.USER_NAME.',$SPACER,$USER_NAME)"/><xsl:text>&#10;</xsl:text>
		<xsl:value-of select="concat('.USER_FIRST_NAME.',$SPACER,$USER_FIRST_NAME)"/><xsl:text>&#10;</xsl:text>
		<xsl:value-of select="concat('.USER_MIDDLE_NAME.',$SPACER,$USER_MIDDLE_NAME)"/><xsl:text>&#10;</xsl:text>
		<xsl:value-of select="concat('.USER_LAST_NAME.',$SPACER,$USER_LAST_NAME)"/><xsl:text>&#10;</xsl:text>
		<xsl:if test="string($USER_ADDR1) or string($EMAIL) or string($PHONE) or string($DEPT1)">
			<xsl:value-of select="concat('.USER_ADDR1_BEGIN.',$USER_ADDR1)"/><xsl:text>&#10;</xsl:text>
			<xsl:value-of select="concat('.EMAIL.',$SPACER,$EMAIL)"/><xsl:text>&#10;</xsl:text>
			<xsl:value-of select="concat('.PHONE.',$SPACER,$PHONE)"/><xsl:text>&#10;</xsl:text>
			<xsl:value-of select="concat('.DEPT.',$SPACER,$DEPT1)"/><xsl:text>&#10;</xsl:text>
			<xsl:text>.USER_ADDR1_END.</xsl:text><xsl:text>&#10;</xsl:text>
		</xsl:if>
		<xsl:if test="string($USER_ADDR2) or string($PHONE) or string($DEPT2)">
			<xsl:value-of select="concat('.USER_ADDR2_BEGIN.',$USER_ADDR2)"/><xsl:text>&#10;</xsl:text>
			<xsl:value-of select="concat('.PHONE.',$SPACER,$PHONE2)"/><xsl:text>&#10;</xsl:text>
			<xsl:value-of select="concat('.DEPT.',$SPACER,$DEPT2)"/><xsl:text>&#10;</xsl:text>
			<xsl:text>.USER_ADDR2_END.</xsl:text><xsl:text>&#10;</xsl:text>
		</xsl:if>
		<xsl:value-of select="concat('.USER_DEPARTMENT.',$SPACER,$USER_DEPARTMENT)"/><xsl:text>&#10;</xsl:text>
		<xsl:if test="$USER_PROFILE!='KEEP'">
			<xsl:value-of select="concat('.USER_PROFILE.',$SPACER,$USER_PROFILE)"/><xsl:text>&#10;</xsl:text>
		</xsl:if>
		<xsl:value-of select="concat('.USER_PRIV_GRANTED.',$SPACER,$USER_PRIV_GRANTED)"/><xsl:text>&#10;</xsl:text>
		<xsl:if test="$USER_PRIV_EXPIRES!='KEEP'">
			<xsl:value-of select="concat('.USER_PRIV_EXPIRES.',$SPACER,$USER_PRIV_EXPIRES)"/><xsl:text>&#10;</xsl:text>
		</xsl:if>
		<xsl:value-of select="concat('.USER_GROUP_ID.',$SPACER,$USER_GROUP_ID)"/><xsl:text>&#10;</xsl:text>
		<xsl:value-of select="concat('.USER_WEB_AUTH.',$SPACER,$USER_GROUP_ID)"/><xsl:text>&#10;</xsl:text>
	</xsl:template>
	<!-- ***************************************************************************************************************************************************** -->
	<!-- Templates -->
	<!-- ***************************************************************************************************************************************************** -->
	<xsl:template match="privgroup">
		<xsl:choose>
			<xsl:when test="text()='organization:medicine'">
				<xsl:value-of select="." />
			</xsl:when>
			<xsl:when test="text()='organization:law'">
				<xsl:value-of select="." />
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="text()='organization:gsb'">
					<xsl:value-of select="." />
				</xsl:if>
			</xsl:otherwise>
			<xsl:text>&#10;</xsl:text>
		</xsl:choose>
	</xsl:template>

    <xsl:template match="affiliation/department">
        <xsl:choose>
            <xsl:when test="organization/@adminid and @affnum = '1'">
                <xsl:value-of select="organization/@adminid"/>
            </xsl:when>
        <xsl:otherwise>
            <xsl:if test="organization/@acadid and @affnum = '1'">
                <xsl:text>:</xsl:text>
                <xsl:value-of select="organization/@acadid"/>
            </xsl:if>
        </xsl:otherwise>
            <xsl:text>&#10;</xsl:text>
        </xsl:choose>
    </xsl:template>
	<!--**********************************************************-->
	<!-- Address - Outputs the first and second line of the address element, city, state, and postal code-->
	<!--**********************************************************-->
	<xsl:template match="address">
		<xsl:text>&#10;</xsl:text><xsl:text>.LINE1.</xsl:text><xsl:value-of select="$SPACER"/>
		<xsl:value-of select="translate(line[position() = 1],'|',':')"/>
		<xsl:if test="line[position() = 2]">
			<xsl:text> </xsl:text>
			<xsl:value-of select="translate(line[position() = 2],'|',':')"/>
		</xsl:if>
		<xsl:text>&#10;</xsl:text><xsl:text>.LINE2.</xsl:text><xsl:value-of select="$SPACER"/>
		<xsl:value-of select="translate(line[position() = 3],'|',':')"/>
		<xsl:if test="line[position() = 4]">
			<xsl:text> </xsl:text>
			<xsl:value-of select="translate(line[position() = 4],'|',':')"/>
		</xsl:if>
		<xsl:text>&#10;</xsl:text><xsl:text>.LINE3.</xsl:text><xsl:value-of select="$SPACER"/>
		<xsl:value-of select="city"/>
		<xsl:text>, </xsl:text>
		<xsl:value-of select="state/@code"/>
		<xsl:text> </xsl:text>
		<xsl:value-of select="postalcode"/>
	</xsl:template>
</xsl:stylesheet>
