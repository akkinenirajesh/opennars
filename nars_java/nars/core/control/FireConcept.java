/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package nars.core.control;

import nars.core.Events;
import nars.core.Memory;
import nars.entity.Concept;
import nars.entity.Task;
import nars.entity.TaskLink;
import nars.entity.TermLink;
import nars.inference.RuleTables;

/** Concept reasoning context - a concept is "fired" or activated by applying the reasoner */
abstract public class FireConcept extends NAL {
    
    public FireConcept(Memory mem, Concept concept, int numTaskLinks) {
        this(mem, concept, numTaskLinks, mem.param.termLinkMaxReasoned.get());
    }
    
    public FireConcept(Memory mem, Concept concept, int numTaskLinks, int termLinkCount) {
        super(mem);
        this.termLinkCount = termLinkCount;
        this.currentConcept = concept;
        this.currentTaskLink = null;
        this.numTaskLinks = numTaskLinks;
    }

    private int numTaskLinks;
    private int termLinkCount;
    
    abstract public void onFinished();
    
    @Override
    public void run() {     
        fire();        
        onFinished();                
    }
    
    
    protected void fire() {

        if (currentTaskLink !=null) {
            fireTaskLink(termLinkCount);
            returnTaskLink(currentTaskLink);
        }
        else {
            for (int i = 0; i < numTaskLinks; i++) {

                if (currentConcept.taskLinks.size() == 0) 
                    return;

                currentTaskLink = currentConcept.taskLinks.takeNext();                    
                if (currentTaskLink == null)
                    return;

                if (currentTaskLink.budget.aboveThreshold()) {
                    fireTaskLink(termLinkCount);                    
                }

                returnTaskLink(currentTaskLink);
            }
        }
        
    }

    
    protected void returnTaskLink(TaskLink t) {
        currentConcept.taskLinks.putBack(t, 
                memory.param.cycles(memory.param.taskLinkForgetDurations), memory);
        
    }
    
    protected void fireTaskLink(int termLinks) {
        final Task task = currentTaskLink.getTarget();
        setCurrentTerm(currentConcept.term);
        setCurrentTaskLink(currentTaskLink);
        setCurrentBeliefLink(null);
        setCurrentTask(task); // one of the two places where this variable is set
        
        if (currentTaskLink.type == TermLink.TRANSFORM) {
            setCurrentBelief(null);
            
            RuleTables.transformTask(currentTaskLink, this); // to turn this into structural inference as below?
            
        } else {            
            while (termLinks > 0) {
                final TermLink termLink = currentConcept.selectTermLink(currentTaskLink, memory.time());
                
                if (termLink == null) {
                    break;
                }
                
                setCurrentBeliefLink(termLink);



                reason(currentTaskLink, termLink);                    

                emit(Events.TermLinkSelect.class, termLink, currentConcept, this);
                memory.logic.REASON.commit(termLink.getPriority());                    

                currentConcept.returnTermLink(termLink);

                termLinks--;
            }
        }
                
        emit(Events.ConceptFire.class, this);
        memory.logic.TASKLINK_FIRE.commit(currentTaskLink.budget.getPriority());
        
    }

    
    protected void reason(TaskLink currentTaskLink, TermLink termLink) {
        RuleTables.reason(currentTaskLink, termLink, this);
    }

    
    @Override
    public String toString() {
        return "FireConcept[" + currentConcept + "," + currentTaskLink + "]";
    }

    
    
}
