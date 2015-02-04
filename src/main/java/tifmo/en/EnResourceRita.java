package tifmo.en;


import rita.wordnet.RiWordnet;
import tifmo.utils.EnUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bdthinh on 10/27/14.
 */
public class EnResourceRita extends EnResource {
	private RiWordnet _wordnet = new RiWordnet();

	public void init(){
		try {
			String filePath = new File("").getCanonicalPath().concat("/resources/dict");
			_wordnet.setWordnetHome(filePath);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void checkAndSetLemmaAndPosWn(EnWord ew){
		String lemmaTemp = ew.get_lemma();
		String posWnTemp = ew.get_token().get_posWN();
		if(lemmaTemp != null && posWnTemp != null)
			return;
		if(lemmaTemp == null) {
			if (posWnTemp == null)
				posWnTemp = _wordnet.getBestPos(ew.get_token().get_word());
			lemmaTemp = _wordnet.getStems(ew.get_token().get_word(), posWnTemp)[0];
		}
		else
			if(posWnTemp == null)
				posWnTemp = _wordnet.getBestPos(ew.get_lemma());
		ew.set_lemma(lemmaTemp);
		ew.get_token().set_posWN(posWnTemp);
	}
	public String getBestPos(EnWord ew){
		ew.get_token().set_posWN(_wordnet.getBestPos(ew.get_lemma()));
		return _wordnet.getBestPos(ew.get_lemma());
	}
	public List<String> getPos(EnWord ew){
		return Arrays.asList(_wordnet.getPos(ew.get_lemma()));
	}
	public List<String> getSynonyms(EnWord ew){
		return Arrays.asList(_wordnet.getSynonyms(ew.get_lemma(), ew.get_token().get_posWN()));
	}
	public List<String> getSynonymsWithPos(EnWord ew, String pos){
		return Arrays.asList(_wordnet.getSynonyms(ew.get_lemma(), pos));
	}
	public List<String> getStems(EnWord ew, String pos){
		return Arrays.asList(_wordnet.getStems(ew.get_lemma(), pos));
	}
	public List<String> getHyperyms(EnWord ew){
		return Arrays.asList(_wordnet.getHypernyms(ew.get_lemma(), ew.get_token().get_posWN()));
	}
	public List<String> getHypernymsWithPos(EnWord ew, String pos){
		return Arrays.asList(_wordnet.getHypernyms(ew.get_lemma(), pos));
	}
	public List<String> getHyponyms(EnWord ew){
		return Arrays.asList(_wordnet.getHyponyms(ew.get_lemma(), ew.get_token().get_posWN()));
	}
	public List<String> getHyponymsWithPos(EnWord ew, String pos){
		return Arrays.asList(_wordnet.getHyponyms(ew.get_lemma(), pos));
	}
	public List<String> getAntonyms(EnWord ew){
		return Arrays.asList(_wordnet.getAntonyms(ew.get_lemma(), ew.get_token().get_posWN()));
	}
	public List<String> getEntailmets(EnWord ew){
		return Arrays.asList(_wordnet.getSimilar(ew.get_lemma(), ew.get_token().get_posWN()));
	}
	public List<String> getDerivations(EnWord ew){
		return Arrays.asList(_wordnet.getDerivedTerms(ew.get_lemma(),ew.get_token().get_posWN()));
	}

	@Override
	public boolean isAntonym(EnWord ewa, EnWord ewb){
		if(!EnUtils.intersection(getSynonyms(ewa), getAntonyms(ewb)).isEmpty()
						|| !EnUtils.intersection(getSynonyms(ewb), getAntonyms(ewa)).isEmpty())
			return true;
		return false;
	}

	@Override
	public boolean isSynonym(EnWord ewa, EnWord ewb){
		if(ewa.isNamedEntity() && ewb.isNamedEntity())
			if(EnUtils.rateMinLCS(ewa.get_lemma(), ewb.get_lemma()) >= 0.5)
				return true;
		if(!ewa.isNamedEntity() && !ewb.isNamedEntity())
			if(!EnUtils.intersection(getSynonyms(ewa), getSynonyms(ewb)).isEmpty())
				return true;
		return false;
	}

	@Override
	public boolean isHyponym(EnWord ewa, EnWord ewb){
		if(ewa.get_token().get_posWN() == "N" && ewb.get_token().get_posWN() == "N")
			if(!EnUtils.intersection(getHyperyms(ewa), getHyponyms(ewb)).isEmpty()
							|| !EnUtils.intersection(getEntailmets(ewa), getSynonyms(ewb)).isEmpty())
				return true;
		return false;
	}

	@Override
	public boolean hasSemRelation(EnWord ewa, EnWord ewb){
		if(ewa.isNamedEntity() && ewb.isNamedEntity())
			if(EnUtils.rateMinLCS(ewa.get_lemma(), ewb.get_lemma()) >= 0.5)
				return true;
		if(!ewa.isNamedEntity() && !ewb.isNamedEntity())
			if(!EnUtils.intersection(getSynonyms(ewa), getSynonyms(ewb)).isEmpty()
							|| !EnUtils.intersection(getSynonyms(ewa), getAntonyms(ewb)).isEmpty()
							|| !EnUtils.intersection(getSynonyms(ewb), getAntonyms(ewa)).isEmpty()
							|| !EnUtils.intersection(getSynonyms(ewa), getDerivations(ewb)).isEmpty()
							|| !EnUtils.intersection(getSynonyms(ewb), getDerivations(ewa)).isEmpty())
				return true;
		return false;
	}

}
