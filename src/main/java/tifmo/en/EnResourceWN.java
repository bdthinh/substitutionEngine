package tifmo.en;

import edu.mit.jwi.item.ISynset;

import edu.mit.jwi.item.Pointer;
import tifmo.coreNLP.Token;
import tifmo.utils.EnUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bdthinh on 10/26/14.
 */
public class EnResourceWN extends EnResource{
	/**
	 * Manager of various language/knowledge resources.
	 *
	 * If you'd like to experiment on new resources, this should be the first
	 * class you want to extend.
	 *
	 * This class provides four methods (namely, `synonym`, `semrel`, `hyponym`, `antonym`).
	 * You can simply override these methods to utilize your own resources.
	 *
	 * All lookup functions in this class are cached to save execution time, and appropriately
	 * synchronized to provide safe re-entry in concurrent programming.
	 *
	 * Currently, the implementation of this class has integrated the following
	 * resources/heuristics:
	 *  - surface string matching for named entities
	 *  - subsumption of time tags recognized by Stanford CoreNLP
	 *  - synonym, hyponym and sematically related words by WordNet
	 * See protected methods for more details.
	 */
	protected static Map<EnWord, List<ISynset>> cacheSynonym = new HashMap<EnWord, List<ISynset>>();
	protected static Map<EnWord, List<ISynset>> cacheHyponym = new HashMap<EnWord, List<ISynset>>();
	protected static Map<EnWord, List<ISynset>> cacheHypernym = new HashMap<EnWord, List<ISynset>>();
	protected static Map<EnWord, List<ISynset>> cacheAntonym = new HashMap<EnWord, List<ISynset>>();
	protected static Map<EnWord, List<ISynset>> cacheEntailment = new HashMap<EnWord, List<ISynset>>();
	protected static Map<EnWord, List<ISynset>> cacheDerivation = new HashMap<EnWord, List<ISynset>>();
	protected static Map<EnWord, List<ISynset>> cacheMeronym = new HashMap<>();
	protected static Map<EnWord, List<ISynset>> cacheHolonym = new HashMap<>();

	public List<ISynset> lookupWordNetSyn(EnWord ew){
		if(ew.isStopWord())
			return new ArrayList<ISynset>();
		else {
			//synchronized
			List<ISynset> iss;
			synchronized (cacheSynonym) {
				iss = cacheSynonym.get(ew);
			}
			if (iss == null) {
				String mypos = ew.get_token().get_posWN();
				String lemma = ew.get_lemma();
				switch (mypos) {
					case "R":
						iss = EnWordNet.synsets(lemma, "R");
						if (iss.isEmpty())
							iss = EnWordNet.synsets(lemma, "J");
						if(iss.isEmpty())
							iss = EnWordNet.synsets(lemma, "0");
						iss.addAll(EnWordNet.getLexical(iss, Pointer.DERIVED_FROM_ADJ));
						break;
					case "J":
					case "N":
					case "V":
						iss = EnWordNet.synsets(lemma, mypos);
						if(iss.isEmpty())
							iss = EnWordNet.synsets(lemma, "0");
						break;
					default: iss = EnWordNet.synsets(lemma, mypos);
				}
				synchronized (cacheSynonym) {
					if(cacheSynonym.get(ew) == null)
						cacheSynonym.put(ew, iss);
				}
			}
			return iss;
		}
	}
	public List<ISynset> lookupWordNetHypo(EnWord ew){
		List<ISynset> iss;
		synchronized (cacheHyponym){
			iss = cacheHyponym.get(ew);
		}
		if(iss == null){
			List<ISynset> syn = lookupWordNetSyn(ew);
			iss = new ArrayList<ISynset>();
			iss.addAll(syn);
			iss.addAll(EnWordNet.getSemantic(syn,Pointer.HYPONYM));
			iss.addAll(EnWordNet.getSemantic(syn,Pointer.HYPONYM_INSTANCE));
			iss.addAll(EnWordNet.getSemantic(syn,Pointer.MERONYM_MEMBER));
			iss.addAll(EnWordNet.getSemantic(syn,Pointer.MERONYM_PART));
			iss.addAll(EnWordNet.getSemantic(syn,Pointer.MERONYM_SUBSTANCE));
		}
		synchronized (cacheHyponym){
			if(cacheHyponym.get(ew) == null)
				cacheHyponym.put(ew, iss);
		}
		return iss;
	}
	public List<ISynset> lookupWordNetMeronym(EnWord ew){
		List<ISynset> iss;
		synchronized (cacheMeronym){
			iss = cacheMeronym.get(ew);
		}
		if(iss == null){
			List<ISynset> syn = lookupWordNetSyn(ew);
			iss = new ArrayList<ISynset>();
			iss.addAll(syn);
			iss.addAll(EnWordNet.getSemantic(syn, Pointer.MERONYM_MEMBER));
			iss.addAll(EnWordNet.getSemantic(syn, Pointer.MERONYM_PART));
			iss.addAll(EnWordNet.getSemantic(syn, Pointer.MERONYM_SUBSTANCE));
		}
		synchronized (cacheMeronym){
			if(cacheMeronym.get(ew) == null)
				cacheMeronym.put(ew, iss);
		}
		return iss;
	}
	public List<ISynset> lookupWordNetHolonym(EnWord ew){
		List<ISynset> iss;
		synchronized (cacheHolonym){
			iss = cacheHolonym.get(ew);
		}
		if(iss == null){
			List<ISynset> syn = lookupWordNetSyn(ew);
			iss = new ArrayList<ISynset>();
			iss.addAll(syn);
			iss.addAll(EnWordNet.getSemantic(syn, Pointer.HOLONYM_MEMBER));
			iss.addAll(EnWordNet.getSemantic(syn, Pointer.HOLONYM_MEMBER));
			iss.addAll(EnWordNet.getSemantic(syn, Pointer.HOLONYM_SUBSTANCE));
		}
		synchronized (cacheHolonym){
			if(cacheHolonym.get(ew) == null)
				cacheHolonym.put(ew, iss);
		}
		return iss;
	}
	public List<ISynset> lookupWordNetHyper(EnWord ew){
		List<ISynset> iss;
		synchronized (cacheHypernym){
			iss = cacheHyponym.get(ew);
		}
		if(iss == null){
			List<ISynset> syn = lookupWordNetSyn(ew);
			iss = new ArrayList<ISynset>();
			iss.addAll(syn);
			iss.addAll(EnWordNet.getSemantic(syn,Pointer.HYPERNYM));
			iss.addAll(EnWordNet.getSemantic(syn,Pointer.HYPERNYM_INSTANCE));
			iss.addAll(EnWordNet.getSemantic(syn,Pointer.HOLONYM_MEMBER));
			iss.addAll(EnWordNet.getSemantic(syn,Pointer.HOLONYM_PART));
			iss.addAll(EnWordNet.getSemantic(syn,Pointer.HOLONYM_SUBSTANCE));
		}
		synchronized (cacheHypernym){
			if(cacheHypernym.get(ew) == null)
				cacheHypernym.put(ew,iss);
		}
		return iss;
	}
	public List<ISynset> lookupWordNetAnt(EnWord ew){
		List<ISynset> iss;

		synchronized (cacheAntonym){
			iss = cacheAntonym.get(ew);
		}
		if(iss == null){
			iss.addAll(EnWordNet.getLexical(lookupWordNetSyn(ew),Pointer.ANTONYM));
		}
		synchronized (cacheAntonym){
			if(cacheAntonym.get(ew) == null)
				cacheAntonym.put(ew,iss);
		}
		return iss;
	}
	public List<ISynset> lookupWordNetEnt(EnWord ew){
		List<ISynset> iss;
		synchronized (cacheEntailment){
			iss = cacheEntailment.get(ew);
		}
		if(iss == null){
			iss = new ArrayList<ISynset>();
			iss.addAll(EnWordNet.getSemantic(lookupWordNetSyn(ew),Pointer.ENTAILMENT));
		}
		synchronized (cacheEntailment){
			if(cacheEntailment.get(ew) == null)
				cacheEntailment.put(ew, iss);
		}
		return iss;
	}
	public List<ISynset> lookupWordNetDeriv(EnWord ew){
		List<ISynset> iss;
		synchronized (cacheDerivation){
			iss = cacheDerivation.get(ew);
		}
		if(iss == null){
			iss = new ArrayList<ISynset>();
			iss.addAll(EnWordNet.getSemantic(lookupWordNetSyn(ew),Pointer.DERIVATIONALLY_RELATED));
		}
		synchronized (cacheDerivation){
			if(cacheDerivation.get(ew) == null)
				cacheDerivation.put(ew, iss);
		}
		return iss;
	}

	@Override
	public boolean isAntonym(EnWord ewa, EnWord ewb){
		if(!EnUtils.intersection(lookupWordNetSyn(ewa), lookupWordNetAnt(ewb)).isEmpty()
						|| !EnUtils.intersection(lookupWordNetSyn(ewb), lookupWordNetAnt(ewa)).isEmpty())
			return true;
		return false;
	}
	@Override
	public boolean isSynonym(EnWord ewa, EnWord ewb){
		if(ewa.isNamedEntity() && ewb.isNamedEntity())
			if(EnUtils.rateMinLCS(ewa.get_lemma(), ewb.get_lemma()) >= 0.5)
				return true;
		if(!ewa.isNamedEntity() && !ewb.isNamedEntity())
			if(!EnUtils.intersection(lookupWordNetSyn(ewa), lookupWordNetSyn(ewb)).isEmpty())
				return true;
		return false;
	}
	@Override
	public boolean isHyponym(EnWord ewa, EnWord ewb){
		if(ewa.get_token().get_posWN().toLowerCase().equals("n") && ewb.get_token().get_posWN().toLowerCase().equals("n"))
			if(!EnUtils.intersection(lookupWordNetHyper(ewa), lookupWordNetHypo(ewb)).isEmpty()
							|| !EnUtils.intersection(lookupWordNetEnt(ewa), lookupWordNetSyn(ewb)).isEmpty() )
				return true;
		return false;
	}
	@Override
	public boolean hasSemRelation(EnWord ewa, EnWord ewb){
		if(ewa.isNamedEntity() && ewb.isNamedEntity())
			if(EnUtils.rateMinLCS(ewa.get_lemma(), ewb.get_lemma()) >= 0.5)
				return true;
		if(!ewa.isNamedEntity() && !ewb.isNamedEntity())
			if(!EnUtils.intersection(lookupWordNetSyn(ewa), lookupWordNetSyn(ewb)).isEmpty()
							|| !EnUtils.intersection(lookupWordNetSyn(ewa), lookupWordNetAnt(ewb)).isEmpty()
							|| !EnUtils.intersection(lookupWordNetSyn(ewb), lookupWordNetAnt(ewa)).isEmpty()
							|| !EnUtils.intersection(lookupWordNetSyn(ewa), lookupWordNetDeriv(ewb)).isEmpty()
							|| !EnUtils.intersection(lookupWordNetSyn(ewb), lookupWordNetDeriv(ewa)).isEmpty())
				return true;
		return false;
	}

	@Override
	public boolean isMeronym(EnWord ewa, EnWord ewb){
		if((ewa.get_token().get_posWN().toLowerCase().equals("n") && ewb.get_token().get_posWN().toLowerCase().equals("n"))
						||(ewa.get_token().get_posWN().toLowerCase().equals("v") && ewb.get_token().get_posWN().toLowerCase().equals("v")))
			if(!EnUtils.intersection(lookupWordNetMeronym(ewa), lookupWordNetHolonym(ewb)).isEmpty())
				return true;
		return false;
	}
	@Override
	public boolean isHolonym(EnWord ewa, EnWord ewb){
		if((ewa.get_token().get_posWN().toLowerCase().equals("n") && ewb.get_token().get_posWN().toLowerCase().equals("n"))
						||(ewa.get_token().get_posWN().toLowerCase().equals("v") && ewb.get_token().get_posWN().toLowerCase().equals("v")))
			if(!EnUtils.intersection(lookupWordNetHolonym(ewa), lookupWordNetMeronym(ewb)).isEmpty())
				return true;
		return false;
	}
	public static boolean isMeronym(String locationA, String locationB){
		Token tokenA = new Token(locationA, "NNP", "LOCATION");
		tokenA.setPosWNAndLemma();
		Token tokenB = new Token(locationB, "NNP", "LOCATION");
		tokenB.setPosWNAndLemma();
		EnWord ewa = new EnWord(tokenA);
		EnWord ewb = new EnWord(tokenB);
		EnResourceWN erwn = new EnResourceWN();
		return erwn.isMeronym(ewa, ewb);
	}
}
