package org.gramlab.core.gramlab.project;

import java.util.Arrays;
import java.util.Comparator;

/**
 * We use our own class to represent ISO language codes, because the java Locale
 * object may change according to new ISO representations.
 * @author paumier
 *
 */
public enum Language {

	other("Other..."),
	aa("Afar"),
	ab("Abkhazian"),
	ae("Avestan"),
	af("Afrikaans"),
	ag("Greek-Ancient"),
	ak("Akan"),
	am("Amharic"),
	an("Aragonese"),
	ar("Arabic"),
	as("Assamese"),
	av("Avaric"),
	ay("Aymara"),
	az("Azerbaijani"),
	ba("Bashkir"),
	be("Belarusian"),
	bg("Bulgarian"),
	bh("Bihari"),
	bi("Bislama"),
	bm("Bambara"),
	bn("Bengali"),
	bo("Tibetan"),
	br("Breton"),
	bs("Bosnian"),
	ca("Catalan"),
	ce("Chechen"),
	ch("Chamorro"),
	co("Corsican"),
	cr("Cree"),
	cs("Czech"),
	cu("Church Slavic"),
	cv("Chuvash"),
	cy("Welsh"),
	da("Danish"),
	de("German"),
	dv("Divehi"),
	dz("Dzongkha"),
	ee("Ewe"),
	el("Greek-Modern"),
	en("English"),
	eo("Esperanto"),
	es("Spanish"),
	et("Estonian"),
	eu("Basque"),
	fa("Persian"),
	ff("Fulah"),
	fi("Finnish"),
	fj("Fijian"),
	fo("Faroese"),
	fr("French"),
	fy("Frisian"),
	ga("Irish"),
	gd("Scottish Gaelic"),
	gl("Gallegan"),
	gn("Guarani"),
	gu("Gujarati"),
	gv("Manx"),
	ha("Hausa"),
	he("Hebrew"),
	hi("Hindi"),
	ho("Hiri Motu"),
	hr("Croatian"),
	ht("Haitian"),
	hu("Hungarian"),
	hy("Armenian"),
	hz("Herero"),
	ia("Interlingua"),
	id("Indonesian"),
	ie("Interlingue"),
	ig("Igbo"),
	ii("Sichuan Yi"),
	ik("Inupiaq"),
	in("Indonesian"),
	io("Ido"),
	is("Icelandic"),
	it("Italian"),
	iu("Inuktitut"),
	iw("Hebrew"),
	ja("Japanese"),
	ji("Yiddish"),
	jv("Javanese"),
	gc("Georgian-Ancient"),
	ka("Georgian-Modern"),
	kg("Kongo"),
	ki("Kikuyu"),
	kj("Kwanyama"),
	kk("Kazakh"),
	kl("Greenlandic"),
	km("Khmer"),
	kn("Kannada"),
	ko("Korean"),
	kr("Kanuri"),
	ks("Kashmiri"),
	ku("Kurdish"),
	kv("Komi"),
	kw("Cornish"),
	ky("Kirghiz"),
	la("Latin"),
	lb("Luxembourgish"),
	lg("Ganda"),
	li("Limburgish"),
	ln("Lingala"),
	lo("Lao"),
	lt("Lithuanian"),
	lu("Luba-Katanga"),
	lv("Latvian"),
	mg("Malagasy"),
	mh("Marshallese"),
	mi("Maori"),
	mk("Macedonian"),
	ml("Malayalam"),
	mn("Mongolian"),
	mo("Moldavian"),
	mr("Marathi"),
	ms("Malay"),
	mt("Maltese"),
	my("Burmese"),
	na("Nauru"),
	nb("Norwegian-Bokmål"),
	nd("North Ndebele"),
	ne("Nepali"),
	ng("Ndonga"),
	nl("Dutch"),
	nn("Norwegian-Nynorsk"),
	nr("South Ndebele"),
	nv("Navajo"),
	ny("Nyanja"),
	oc("Occitan"),
	oj("Ojibwa"),
	om("Oromo"),
	or("Oriya"),
	os("Ossetian"),
	pa("Panjabi"),
	pi("Pali"),
	pl("Polish"),
	ps("Pushto"),
	pt("Portuguese-Portugal"),
	pb("Portuguese-Brazil"),
	qu("Quechua"),
	rm("Raeto-Romance"),
	rn("Rundi"),
	ro("Romanian"),
	ru("Russian"),
	rw("Kinyarwanda"),
	sa("Sanskrit"),
	sc("Sardinian"),
	sd("Sindhi"),
	se("Northern Sami"),
	sg("Sango"),
	si("Sinhalese"),
	sk("Slovak"),
	sl("Slovenian"),
	sm("Samoan"),
	sn("Shona"),
	so("Somali"),
	sq("Albanian"),
	sr("Serbian-Latin"),
	sz("Serbian-Cyrillic"),
	ss("Swati"),
	st("Southern Sotho"),
	su("Sundanese"),
	sv("Swedish"),
	sw("Swahili"),
	sy("Syriac"),
	ta("Tamil"),
	te("Telugu"),
	tg("Tajik"),
	th("Thai"),
	ti("Tigrinya"),
	tk("Turkmen"),
	tl("Tagalog"),
	tn("Tswana"),
	to("Tonga"),
	tr("Turkish"),
	ts("Tsonga"),
	tt("Tatar"),
	tw("Twi"),
	ty("Tahitian"),
	ug("Uighur"),
	uk("Ukrainian"),
	ur("Urdu"),
	uz("Uzbek"),
	ve("Venda"),
	vi("Vietnamese"),
	vo("Volapük"),
	wa("Walloon"),
	wo("Wolof"),
	xh("Xhosa"),
	yi("Yiddish"),
	yo("Yoruba"),
	za("Zhuang"),
	zh("Chinese"),
	zu("Zulu");
	
	private String fullname;
	
	private Language(String fullname) {
		this.fullname=fullname;
	}
	
	@Override
	public String toString() {
		if (fullname.equals("Other...")) return fullname;
		return fullname+" ("+this.name()+")";
	}

	/**
	 * Tries to identify the given language
	 * @param name
	 * @return
	 */
	public static String getLanguageForUnitexName(String name) {
		for (Language l:values()) {
			String a=name.toLowerCase();
			if (a.startsWith(l.fullname.toLowerCase())) {
				return l.name();
			}
		}
		if (name.equalsIgnoreCase("Georgian (Ancient)")) return gc.name();
		if (name.equalsIgnoreCase("Greek (Ancient)")) return ag.name();
		if (name.equalsIgnoreCase("Greek (Modern)")) return el.name();
		if (name.equalsIgnoreCase("Norwegian (Bokmal)")) return nb.name();
		if (name.equalsIgnoreCase("Norwegian (Nynorsk)")) return nn.name();
		if (name.equalsIgnoreCase("Portuguese (Portugal)")) return pt.name();
		if (name.equalsIgnoreCase("Portuguese (Brazil)")) return pb.name();
		if (name.equalsIgnoreCase("Serbian (Cyrillic)")) return sz.name();
		if (name.equalsIgnoreCase("Serbian (Latin)")) return sr.name();
		return name.toLowerCase();
	}
	
	private static Language[] sorted=null;
	
	public static Language[] getSortedValues() {
		if (sorted==null) {
			sorted=values().clone();
			Comparator<Language> c=new Comparator<Language>() {

				@Override public int compare(Language o1,Language o2){
					if (o1.fullname.equals("Other...")) return -1;
					if (o2.fullname.equals("Other...")) return 1;
					return o1.toString().compareTo(o2.toString());
				}
			};
			Arrays.sort(sorted,c);
		}
		return sorted;
	}
	
	public static Language getLanguage(String name) {
		name=getLanguageForUnitexName(name);
		for (Language l:values()) {
			if (l.name().equals(name)) return l;
		}
		return null;
	}
	
}
