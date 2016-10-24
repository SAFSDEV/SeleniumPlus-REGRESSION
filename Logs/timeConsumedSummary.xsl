<xsl:stylesheet version='2.0'
	xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
	xmlns:xs='http://www.w3.org/2001/XMLSchema'
	xmlns='http://www.w3.org/TR/REC-html40'>

	<xsl:output encoding="UTF-8" method="html" omit-xml-declaration="yes" />

	<xsl:variable name="colorPass">#000000</xsl:variable>
	<xsl:variable name="colorWarn">#0000ff</xsl:variable>
	<xsl:variable name="colorAlert">#ff0000</xsl:variable>
	
	<xsl:variable name="thresholdSecondWarn" select="number(5)"/>
	<xsl:variable name="thresholdSecondAlert" select="number(10)"/>
	
	<xsl:variable name="idConsumeTimeDetailTable">idConsumeTimeDetailTable</xsl:variable>
	<xsl:variable name="idConsumeTimeSummaryTable">idConsumeTimeSummaryTable</xsl:variable>
	<xsl:variable name="idConsumeTimeTable_normalCount">idConsumeTimeTable_normalCount</xsl:variable>
	<xsl:variable name="idConsumeTimeTable_warnCount">idConsumeTimeTable_warnCount</xsl:variable>
	<xsl:variable name="idConsumeTimeTable_alertCount">idConsumeTimeTable_alertCount</xsl:variable>
	<xsl:variable name="idConsumeTimeTable_totalCount">idConsumeTimeTable_totalCount</xsl:variable>
	<xsl:variable name="idConsumeTimeTable_warnLinks">idConsumeTimeTable_warnLinks</xsl:variable>
	<xsl:variable name="idConsumeTimeTable_alertLinks">idConsumeTimeTable_alertLinks</xsl:variable>

	<xsl:variable name="debut">
		<xsl:call-template name="getW3cDateTime">
			<xsl:with-param name="safs_date" select="/SAFS_LOG/LOG_OPENED/@date" />
			<xsl:with-param name="safs_time" select="/SAFS_LOG/LOG_OPENED/@time" />
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="fin">
		<xsl:call-template name="getW3cDateTime">
			<xsl:with-param name="safs_date" select="/SAFS_LOG/LOG_CLOSED/@date" />
			<xsl:with-param name="safs_time" select="/SAFS_LOG/LOG_CLOSED/@time" />
		</xsl:call-template>
	</xsl:variable>
	<!-- https://www.w3.org/TR/xmlschema-2/#duration -->
    <xsl:variable name="total_duration"><xsl:value-of select="xs:dateTime($fin)-xs:dateTime($debut)"/></xsl:variable>
    <xsl:variable name="total_seconds"><xsl:value-of select="hours-from-duration($total_duration)*3600+minutes-from-duration($total_duration)*60+seconds-from-duration($total_duration)"/></xsl:variable>
    
	<xsl:template match="/">
		<HTML>
			<HEAD>
			    <script type="text/javascript" src="shared.js"></script> 
				<SCRIPT language="JavaScript">
					document.write("<TITLE>"+ getTestName() +" Test Results</TITLE>");
				</SCRIPT>
			</HEAD>
			<BODY>
				<SCRIPT language="JavaScript">
					document.write("<H2>Test: "+ getTestName() +"</H2>");
				</SCRIPT>
				<HR />
				<TABLE>
					<TR>
						<TD>Test Start: </TD>
						<TD style="font-weight:bold;"><xsl:value-of select="$debut"/></TD>
					</TR>
					<TR>
						<TD>Test Finished: </TD>
						<TD style="font-weight:bold;"><xsl:value-of select="$fin"/></TD>
					</TR>
					<TR>
						<TD>Time Consumed: </TD>
						<TD style="font-weight:bold;">
							<xsl:value-of select="$total_duration" />:  :<xsl:value-of select="$total_seconds" /><xsl:text> seconds.</xsl:text>
						</TD>
					</TR>
				</TABLE>

				
				<H3>Consumed Time Summary:</H3>
				<TABLE border="2" cellpadding="5" id="{$idConsumeTimeSummaryTable}">
				    <TR style="font-weight:bold;"><TD>Level</TD><TD>Threshold (seconds)</TD><TD>Count/Seconds</TD><TD>Records Over Threshold</TD></TR>
				    <TR ><TD>Normal</TD><TD>0</TD><TD id="{$idConsumeTimeTable_normalCount}"></TD><TD></TD></TR>
				    <TR style="font-weight:bold;color:{$colorWarn};"><TD>Warn</TD><TD><xsl:value-of select='$thresholdSecondWarn'/></TD><TD id="{$idConsumeTimeTable_warnCount}"></TD><TD id="{$idConsumeTimeTable_warnLinks}"></TD></TR>
				    <TR style="font-weight:bold;color:{$colorAlert};"><TD>Alert</TD><TD><xsl:value-of select='$thresholdSecondAlert'/></TD><TD id="{$idConsumeTimeTable_alertCount}"></TD><TD id="{$idConsumeTimeTable_alertLinks}"></TD></TR>
				    <TR ><TD>TOTAL:</TD><TD></TD><TD id="{$idConsumeTimeTable_totalCount}"></TD><TD></TD></TR>
				</TABLE>
				
				<H3>Consumed Time Details:</H3>
				<TABLE border="2" cellpadding="5" id="{$idConsumeTimeDetailTable}">
				    <TR style="font-weight:bold;"><TD>Test Record</TD><TD>Start</TD><TD>End</TD><TD>Seconds</TD></TR>
					<xsl:for-each select="/SAFS_LOG/LOG_MESSAGE">
						<xsl:call-template name="consum_time_detail" />
					</xsl:for-each>
				</TABLE>

                <SCRIPT language="JavaScript">
                  fillSummaryTable([
                                        "<xsl:value-of select='$idConsumeTimeDetailTable'/>",
                                        "<xsl:value-of select='$idConsumeTimeSummaryTable'/>"
                                   ], 
                                   [    
                                        <xsl:value-of select='$thresholdSecondWarn'/>,
                                        <xsl:value-of select='$thresholdSecondAlert'/> 
                                   ],
                                   [
                                        "<xsl:value-of select='$idConsumeTimeTable_normalCount'/>",
                                        "<xsl:value-of select='$idConsumeTimeTable_warnCount'/>",
                                        "<xsl:value-of select='$idConsumeTimeTable_alertCount'/>",
                                        "<xsl:value-of select='$idConsumeTimeTable_totalCount'/>",
                                        "<xsl:value-of select='$idConsumeTimeTable_warnLinks'/>",
                                        "<xsl:value-of select='$idConsumeTimeTable_alertLinks'/>"
                                   ]);
                 </SCRIPT>
			</BODY>
		</HTML>
	</xsl:template>

    <!--  Generate table content showing time consumed by each test record -->
	<xsl:template name="consum_time_detail">
		<xsl:variable name="start">
			<xsl:call-template name="getW3cDateTime">
				<xsl:with-param name="safs_date" select="preceding-sibling::LOG_MESSAGE[1]/@date"/>
				<xsl:with-param name="safs_time" select="preceding-sibling::LOG_MESSAGE[1]/@time"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="end">
            <xsl:call-template name="getW3cDateTime">
                <xsl:with-param name="safs_date" select="@date"/>
                <xsl:with-param name="safs_time" select="@time"/>
            </xsl:call-template>
		</xsl:variable>

		<xsl:variable name="duration">
			<xsl:choose>
				<xsl:when test="not($start='-T' or $end='-T')">
					<xsl:value-of select="xs:dateTime($end)-xs:dateTime($start)" />
				</xsl:when>
				<xsl:otherwise>
					PT0S
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="rowid">row<xsl:value-of select="position()"/></xsl:variable>
		
		<xsl:variable name="duration_hour"><xsl:value-of select="hours-from-duration($duration)"/></xsl:variable>
		<xsl:variable name="duration_minute"><xsl:value-of select="minutes-from-duration($duration)"/></xsl:variable>
		<xsl:variable name="duration_second"><xsl:value-of select="seconds-from-duration($duration)"/></xsl:variable>
		<xsl:variable name="total_duration_in_second"><xsl:value-of select="$duration_hour*60*60+$duration_minute*60+$duration_second"/></xsl:variable>
		<xsl:variable name="time_color">
		  <xsl:choose>
              <xsl:when test="$total_duration_in_second>$thresholdSecondAlert"><xsl:value-of select="$colorAlert"/></xsl:when>
		      <xsl:when test="$total_duration_in_second>$thresholdSecondWarn"><xsl:value-of select="$colorWarn"/></xsl:when>
		      <xsl:otherwise><xsl:value-of select="$colorPass"/></xsl:otherwise>
		  </xsl:choose>
		</xsl:variable>

		<xsl:if test="not($duration_hour=0 and $duration_minute=0 and $duration_second=0)">
			<TR id="{$rowid}" style="color:{$time_color};">
				<TD style="font-style:oblique;"><xsl:value-of select="@type" />: :<xsl:value-of select="./MESSAGE_TEXT/text()" /></TD>
				<TD style="font-weight:bold;white-space:nowrap;"><xsl:value-of select="$start" /></TD>
				<TD style="font-weight:bold;white-space:nowrap;"><xsl:value-of select="$end" /></TD>
				<TD style="font-weight:bold;" id="{$rowid}_col4">
					<xsl:value-of select="$total_duration_in_second"/>
				</TD>
			</TR>
		</xsl:if>
		
	</xsl:template>

    <!-- Convert SAFS date-time into w3c standard date-time -->
    <xsl:template name="getW3cDateTime"><!-- https://www.w3.org/TR/xmlschema-2/#dateTime -->
        <xsl:param name="safs_date" /><!-- MM-dd-yyyy -->
        <xsl:param name="safs_time" /><!-- HH:mm:ss -->

        <!-- return the standard w3c date-time yyyy-MM-ddTHH:mm:ss  -->
        <xsl:value-of select="concat(substring($safs_date,7,4), '-' , substring($safs_date,1,5))" />T<xsl:value-of select="$safs_time" />

    </xsl:template>

</xsl:stylesheet>
    
