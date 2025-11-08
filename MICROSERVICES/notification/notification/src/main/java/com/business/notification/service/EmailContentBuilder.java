package com.business.notification.service;

import com.business.notification.dataclasses.NotificationRequest;
import com.business.notification.dataclasses.InlineContent;
import com.business.notification.utils.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class EmailContentBuilder {
	private static final String ROOT_ENV_KEY="com.stayeaze.email";
	private static final String IMAGES="images";
	private static final String HEADER="header";
	private static final String FOOTER="footer";
	private static final String BODY="body";
	private static final String IMAGE="image";
	private static final String TEXT="text";
	private static final String FILE="file";
	private static final String SUBJECT = "subject";
	private static final String INLINE_IMAGES="inlineImages";
	private static final String STRING_FORMAT_WITH_3 = "%s.%s.%s";
	private static final String STRING_FORMAT_WITH_2 = "%s.%s";

	/** (String)int between 1= high and 3 = low. */
	@Value("${com.stayeaze.email.priority:2}")
	private String priority;
	/** seems to be needed for MS Outlook. where it returns a string of high /normal /low. */
	@Value("${com.stayeaze.email.importance:normal}")
	private String importance;

	@Autowired private Environment env;
	@Autowired private ApplicationContext context;
	
	/**
	 * @param request
	 * @param message
	 * @return
	 * @throws MessagingException
	 */
	void createEmail(final NotificationRequest request, MimeMessage message) throws MessagingException,MalformedURLException {
		//Adding Subject
		String emailCode = request.getEmailCode();
		log.info("Email Code in create email : {}", emailCode);
		String preferredLanguage = request.getPreferredLanguage();
		log.info("preferredLanguage in create email : {}", preferredLanguage);
		if (!CommonUtility.isNullOrEmpty(preferredLanguage) && preferredLanguage.equals("es")) {
			request.setEmailCode(String.format("%s.%s", preferredLanguage, emailCode));
			log.info("set email code if es : {}",String.format("%s.%s", preferredLanguage, emailCode) );
		}
		final String subject=getSubject(request.getEmailCode());
		log.info("subject and emailcode: {}, {}", subject, request.getEmailCode());

		message.setHeader("X-Priority",priority);
		message.setHeader("Importance", importance);
		
		message.setSubject(addDynamicContentToSubject(request, subject));
		// use the true flag to indicate you need a multipart message
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
		helper.setTo(request.getTo().split(","));
		StringBuilder cc=new StringBuilder();
		StringBuilder bcc=new StringBuilder();


		String bccEmailsFromConfig = getDefaultBCCemails();
		log.info("default bcc emails from configuration: {}", bccEmailsFromConfig);
		if(!CommonUtility.isNullOrEmpty(bccEmailsFromConfig)) {
			String [] bccEmailsList = bccEmailsFromConfig.split(",");
			log.info("BCC Emails: {}", (Object)bccEmailsList);
			helper.setBcc(bccEmailsList);
		}

		if(!CommonUtility.isNullOrEmpty(request.getCc())) {
		    String ccEmails = StringUtils.collectionToCommaDelimitedString(request.getCc());
			//request.getCc().forEach(arg->{cc.append(String.format("%s,", arg));});
            String [] ccEmailsList = ccEmails.split(",");
            helper.setCc(ccEmailsList);

		}

		if(!CommonUtility.isNullOrEmpty(request.getBcc())) {
			request.getBcc().forEach(arg->{bcc.append(String.format("%s,", arg));});
			helper.setBcc(bcc.toString());
		}


		final String from=getFromAddress(request.getFrom(),request.getEmailCode());
		helper.setFrom(from);
		// use the true flag to indicate the text included is HTML
		StringBuilder emailText= new StringBuilder();
		emailText.append(createHeader(request.getEmailCode()));
		emailText.append(createBody(request.getEmailCode()));
		emailText.append(createFooter(request.getEmailCode()));
		String emailContent=emailText.toString();
		//adding dynamic contents
		addDyanamicContents(request, helper, emailContent);
	}


	private String addDynamicContentToSubject(NotificationRequest request, String subject) {

		//DYANAMIC TEXT DATA
		//replacing dynamic text
		log.info("subject is not blank and going replacing dynamic text");
		for(int i=0; 
				null!=request.getInlineContentsForSubject() 
				&& i<request.getInlineContentsForSubject().size()
				&& TEXT.equalsIgnoreCase(request.getInlineContentsForSubject().get(i).getResourceType());
				i++){
			log.debug("Replacing All TEXT# {}:{}",request.getInlineContentsForSubject().get(i).getContentId()
					,request.getInlineContentsForSubject().get(i).getResourceValue());
			subject=subject.replaceAll(
					String.format("cid:%s", request.getInlineContentsForSubject().get(i).getContentId()),
					request.getInlineContentsForSubject().get(i).getResourceValue());
		}
		return subject;
	}


	/**
	 * @param from
	 * @param emailCode
	 * @return
	 */
	private String getFromAddress(String from, final String emailCode) {
		if(StringUtils.isEmpty(from)) {
			log.debug("From feild is empty");
			from=String.format("%s <%s>", 
					env.getProperty(String.format(STRING_FORMAT_WITH_3, ROOT_ENV_KEY,emailCode,"sender.name"),""),
					env.getProperty(String.format(STRING_FORMAT_WITH_3, ROOT_ENV_KEY,emailCode,"sender.email"),""));
			if(from.length()==3) {// length validation due format
				log.debug("Returning default From email Address.");
				from=String.format("%s <%s>", 
						env.getProperty(String.format(STRING_FORMAT_WITH_2, ROOT_ENV_KEY,"sender.name"),""),
						env.getProperty(String.format(STRING_FORMAT_WITH_2, ROOT_ENV_KEY,"sender.email"),""));
			}
		}
		return from;
	}


	/**
	 * @param request
	 * @param helper
	 * @param emailContent
	 * @return
	 * @throws MessagingException
	 */
	private String addDyanamicContents(final NotificationRequest request, MimeMessageHelper helper, String emailContent)
			throws MessagingException,MalformedURLException {
		log.info("adding dyanmic content");
		if(!StringUtils.isEmpty(emailContent)) {
			log.trace("content: ",emailContent);
			addTextContents(request, helper, emailContent);
			addImageContens(request, helper);
			addFileContents(request,helper);

		}
		return emailContent;
	}

	private void addFileContents(NotificationRequest request, MimeMessageHelper helper) throws MessagingException {
		log.info("Inside file content method.");
		for (int i = 0;
			 null != request.getInlineContents()
					 && i < request.getInlineContents().size();
			 i++) {
			if(!FILE.equalsIgnoreCase(request.getInlineContents().get(i).getResourceType())){
				log.info("Resource Type is not FILE so skipping.");
				continue;
			}
			log.info("Inside file content method1. : {}", request.getInlineContents());

			InlineContent ilc = request.getInlineContents().get(i);
			log.info("Inside file content method2. : {}", ilc);
				FileSystemResource file = new FileSystemResource(
						ilc.getObject()
				);
				log.info("File name : {} , File : {}",file.getFilename(), file);

				helper.addAttachment(file.getFilename(), file);


		}
	}


	/**
	 * @param request
	 * @param helper
	 * @param emailContent
	 * @throws MessagingException
	 */
	private void addTextContents(final NotificationRequest request, MimeMessageHelper helper, String emailContent)
			throws MessagingException,MalformedURLException {
		//DYANAMIC TEXT DATA
		//replacing dynamic text
		log.info("emailContent is not blank and going replacing dynamic text");
		for(int i=0; 
				null!=request.getInlineContents() 
				&& i<request.getInlineContents().size();
				i++){
			if (!TEXT.equalsIgnoreCase(request.getInlineContents().get(i).getResourceType())){
				log.info("The resource type is not TEXT, so skipping.");
				continue;
			}
			log.debug("Replacing All TEXT# {}:{}",request.getInlineContents().get(i).getContentId()
					,request.getInlineContents().get(i).getResourceValue());
			emailContent=emailContent.replaceAll(
					String.format("cid:%s", request.getInlineContents().get(i).getContentId()),
					request.getInlineContents().get(i).getResourceValue());
		}
		
		emailContent=env.getProperty(String.format(STRING_FORMAT_WITH_2, ROOT_ENV_KEY,"b2b.html.head"), "")+emailContent+env.getProperty(String.format(STRING_FORMAT_WITH_2, ROOT_ENV_KEY,"b2b.html.tail"), "");
		helper.setText(emailContent, true);
	}


	/**
	 * @param request
	 * @param helper
	 * @throws MessagingException
	 */


	/**
	 * @param emailCode
	 * @return
	 */
	private String getSubject(final String emailCode) {
		log.info("emailCode: {}", emailCode);
		log.info("subjectkey: {}", String.format(STRING_FORMAT_WITH_3, ROOT_ENV_KEY,emailCode,SUBJECT));
		return env.getProperty(String.format(STRING_FORMAT_WITH_3, ROOT_ENV_KEY,emailCode,SUBJECT),"");
	}


	/**
	 * @param helper
	 * @param inLineContentId
	 * @param imagePath
	 * @throws MessagingException
	 * @throws MalformedURLException
	 */
	private void addInlineContent(MimeMessageHelper helper,final String inLineContentId	,final String imagePath
			) throws MessagingException {
		Resource imageResource=context.getResource(String.format("classpath:images/%s",imagePath));
		log.debug("imagepath:{} isExits:{}",imageResource.getFilename(),imageResource.exists());
		if(imageResource.exists()) {
			helper.addInline(inLineContentId,imageResource);
		}
	}

	/**
	 * @param emailCode
	 * @return
	 */
	private String createBody(final String emailCode) {
		String body=env.getProperty(String.format(STRING_FORMAT_WITH_2, ROOT_ENV_KEY,BODY),"");
		return env.getProperty(String.format(STRING_FORMAT_WITH_3, ROOT_ENV_KEY,emailCode,BODY),body);
	}

	private String getDefaultBCCemails() {
		log.info("Inside default bcc email fetch method...");
		return env.getProperty(String.format(STRING_FORMAT_WITH_2, ROOT_ENV_KEY, "bcc.emails"), "");

	}

	/**
	 * @param emailCode
	 * @return
	 */
	private String createHeader(final String emailCode) {
		String defaultHeader=env.getProperty(String.format(STRING_FORMAT_WITH_2, ROOT_ENV_KEY,HEADER),"");
		return env.getProperty(String.format(STRING_FORMAT_WITH_3, ROOT_ENV_KEY,emailCode,HEADER),defaultHeader);
	}
	
	/**
	 * @param emailCode
	 * @return
	 */
	private String createFooter(final String emailCode) {
		String defaultFooter=env.getProperty(String.format(STRING_FORMAT_WITH_2,ROOT_ENV_KEY,FOOTER),"");
		return env.getProperty(String.format(STRING_FORMAT_WITH_3,ROOT_ENV_KEY,emailCode,FOOTER),defaultFooter);
	}

	private void addImageContens(final NotificationRequest request, MimeMessageHelper helper)
			throws MessagingException {

		Set<String> usedCids = new HashSet<>();

		// Combine all email parts to scan for cid references
		String emailContent = createHeader(request.getEmailCode())
				+ createBody(request.getEmailCode())
				+ createFooter(request.getEmailCode());

		// Extract all cid:xxx references from HTML content
		Matcher matcher = Pattern.compile("cid:([a-zA-Z0-9_\\-]+)").matcher(emailContent);
		while (matcher.find()) {
			usedCids.add(matcher.group(1));
		}

		// Add only those inline images which are actually used in the HTML (via cid)
		for (String cid : usedCids) {
			String imageFileName = env.getProperty(String.format("%s.%s.%s", ROOT_ENV_KEY, IMAGES, cid), "");
			if (StringUtils.hasText(imageFileName)) {
				log.debug("Adding image for CID '{}': {}", cid, imageFileName);
				addInlineContent(helper, cid, imageFileName);
			} else {
				log.warn("No image mapping found for CID '{}'", cid);
			}
		}
	}



}
