<xsl:stylesheet version='1.0'
		xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
		xmlns='http://www.w3.org/TR/REC-html40'>
    
    <xsl:output encoding="UTF-8" method="html" omit-xml-declaration="yes" />

    <xsl:variable name="warncolor">yellow</xsl:variable>
    <xsl:variable name="failcolor">#FF8080</xsl:variable>

    <xsl:template match="/">
    <HTML>

    <SCRIPT language="JavaScript">        
        function getTestName() {
	      var uri = new String(document.location);
	      var index = uri.lastIndexOf("/");
	      var test = uri.substring(index +1, uri.length);
	      var index = test.indexOf(".");
	      return test.substring(0, index);
        } 
    </SCRIPT>

        <HEAD>     
            <SCRIPT language="JavaScript">
                document.write("<TITLE>"+ getTestName() +" Failed Test Results</TITLE>");
            </SCRIPT>
        </HEAD>
    <BODY>
    
        <SCRIPT language="JavaScript">
            document.write("<H2>Test: "+ getTestName() +"</H2>");
        </SCRIPT>
        Report Generated: 
            <SCRIPT language="JavaScript">
                document.write(document.lastModified);
            </SCRIPT>
        <HR />
        <TABLE>
            <TR><TD>Test Start: </TD>
                <TD><B>
                    <xsl:value-of select="/SAFS_LOG/LOG_OPENED/@time"/><xsl:text>&#160;&#160;</xsl:text>
                    </B>
                    <xsl:value-of select="/SAFS_LOG/LOG_OPENED/@date"/>
                </TD>
            </TR>
            <TR><TD>Test Finished: </TD>
                <TD><B>
                    <xsl:value-of select="/SAFS_LOG/LOG_CLOSED/@time"/><xsl:text>&#160;&#160;</xsl:text>
                    </B>
                    <xsl:value-of select="/SAFS_LOG/LOG_CLOSED/@date"/>
                </TD>
            </TR>
    	</TABLE>

		<H4>
	   		<font color="red">Go back </font> the <a href="./Regression_Summary.htm"> Testing Summary Reports. </a>
	   	</H4>
	    	
		<!-- Generate the Unexpected Failure Report -->
		<!-- Find the 'UNEXPECTED' line with type 'FAILED' -->
		<xsl:if test="/SAFS_LOG/LOG_MESSAGE[@type ='FAILED' and contains(./MESSAGE_TEXT/text(), 'UNEXPECTED')] != '' ">	
			<div style="color: white; background-color: #33ccff; font-size: 18px; font-weight: 400">
		    	<H3>Logged Unexpected Failures:</H3>			
	    	</div>	    		    	
		</xsl:if>   
		 	
    	<xsl:for-each select="/SAFS_LOG/LOG_MESSAGE[@type ='FAILED' and contains(./MESSAGE_TEXT/text(), 'UNEXPECTED')] ">
    		
	    	<TABLE border="2" cellpadding="5" >	    		
	    		<!-- Ignore the last whole 'Regression' UNEXPECTED line  -->
				<xsl:if test="./MESSAGE_TEXT[not(contains(text(), 'Regression'))] ">
										
						<!-- ========================= -->
						<!-- 1. COUNTER upper-boundary -->
		 				<xsl:if test="preceding-sibling::*[contains(@type, 'COUNTER')][2]">
			 				<TR style="background-color: yellow;"> <TD>
				 				[Start position of one unexpected error] &gt;&gt;&gt; 
				 				<b> 
				 					<xsl:value-of select="preceding-sibling::*[contains(@type, 'COUNTER')][2]/@type" />	 					 
				 					<font style="color: red;">
					 					<xsl:value-of select="preceding-sibling::*[contains(@type, 'COUNTER')][2]" />
				 					</font> 					
				 				</b>			 					
			 				</TD> </TR> 	
			    		</xsl:if>
			    		
			    		
			    		
			    		<!-- ================================== -->
			    		<!-- 2. Contents between two boundaries -->
			    		<xsl:variable name="ns1" select="(preceding-sibling::*[contains(@type, 'COUNTER')][2])/following-sibling::*" />							
						<xsl:variable name="ns2" select="(preceding-sibling::*[contains(@type, 'COUNTER')][1])/following-sibling::*" />					
						<xsl:variable name="beContents" select="$ns1[count(.| $ns2) != count($ns2)]" />							
											
						<xsl:if test="$beContents != ''">					
							<xsl:for-each select="$beContents">
									<xsl:if test="@type = 'FAILED'">
										<xsl:call-template name="sum_fail" />
									</xsl:if>
							</xsl:for-each>			
											
						</xsl:if>
			    		
			    		
			    		
			    		<!-- ========================= -->
			    		<!-- 3. COUNTER lower-boundary -->
			    		<xsl:if test="preceding-sibling::*[contains(@type, 'COUNTER')][1]">		  
							<TR> <TD>	 	
				 				[End position of one unexpected error] &lt;&lt;&lt;  
								<b> 
					    			<!-- <span class="glyphicon glyphicon-menu-left"></span> -->
						    		<xsl:value-of select="preceding-sibling::*[contains(@type, 'COUNTER')][1]/@type" />
						    		
						    		<font color="red">
							    		<xsl:value-of select="preceding-sibling::*[contains(@type, 'COUNTER')][1]" />
				 					</font>
				 				</b>							    			 
			 				</TD> </TR>
			    		</xsl:if>
			    			 
	    		</xsl:if> 	    		
	    	</TABLE>  
	    	 
	    	<br /><br />
    	</xsl:for-each>	    	
    	
    	<hr />
    	
    	<div style="color: white; background-color: #33ccff; font-size: 18px; font-weight: 400">
	    	<H3>Logged Failures:</H3>			
    	</div>	
    	
    	<TABLE border="2" cellpadding="5" >
    	
    	<xsl:for-each select="/SAFS_LOG/LOG_MESSAGE[@type = 'FAILED']">
    	    <xsl:call-template name="sum_fail" />
    	</xsl:for-each>
    	
    	</TABLE>

    </BODY>
    </HTML>
    </xsl:template>


    <xsl:template name="sum_fail">
        <TR>
        <xsl:choose>
            <xsl:when test="contains(./MESSAGE_TEXT/text(),'Could not find benchmark file')">
		<TD bgcolor="{$warncolor}"><PRE><xsl:copy-of select="." /></PRE></TD>
            </xsl:when>
            <xsl:when test="contains(./MESSAGE_TEXT/text(),'UNABLE TO TRANSFER EXECUTION')">
		<TD bgcolor="{$failcolor}"><PRE><xsl:copy-of select="." /></PRE></TD>
            </xsl:when>
            <xsl:when test="contains(./MESSAGE_TEXT/text(),'VerifyGUIImageToFile')">        
                <xsl:if test="contains(./MESSAGE_TEXT/text(),'match benchmark')">
		    <TD>
		    <xsl:variable name="detail" select="./MESSAGE_DETAILS/text()"/>
		    <xsl:variable name="dif"    select="substring-after($detail,'; Dif at ')"/>
		    <xsl:variable name="html"><xsl:value-of select="concat($dif,'DIFF.HTML')"/></xsl:variable>
		    <A href="file://{$html}"><xsl:value-of select="$html"/></A>	
		    <PRE><xsl:value-of select="." /></PRE>
		    </TD>
		</xsl:if>
            </xsl:when>
            <xsl:otherwise>
	        <xsl:variable name="detail"  select="./MESSAGE_DETAILS/text()"/>
		<xsl:variable name="compare" select="substring-after($detail,'; Comparator file at ')"/>
		<xsl:variable name="dif"     select="substring-after($detail,'; Dif at ')"/>
		<xsl:variable name="bat">
		    <xsl:if test="string-length($compare) = 0">
		        <xsl:if test="string-length($dif) > 0"><xsl:value-of select="concat($dif,'DIFF.BAT')"/></xsl:if>
		    </xsl:if>
		    <xsl:if test="string-length($compare) > 0"><xsl:value-of select="$compare"/></xsl:if>
		</xsl:variable>
		<xsl:choose>
		    <xsl:when test="string-length($bat) > 0">
	            <TD>
		        <A href="file://{$bat}"><xsl:value-of select="$bat"/></A>
			<PRE><xsl:value-of select="." /></PRE>
	            </TD>
		    </xsl:when>
		    <xsl:otherwise>
	            <TD>
			<PRE><xsl:value-of select="." /></PRE>
	            </TD>
		    </xsl:otherwise>
		</xsl:choose>
    	    </xsl:otherwise>
    	</xsl:choose>
        </TR>
    </xsl:template>

</xsl:stylesheet>
