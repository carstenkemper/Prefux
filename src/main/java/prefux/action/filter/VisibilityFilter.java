package prefux.action.filter;

import java.util.Iterator;

import prefux.Visualization;
import prefux.action.GroupAction;
import prefux.data.expression.OrPredicate;
import prefux.data.expression.Predicate;
import prefux.util.PrefuseLib;
import prefux.visual.VisualItem;
import prefux.visual.expression.VisiblePredicate;

/**
 * Filter Action that sets visible all items that meet a given Predicate
 * condition and sets all other items invisible.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class VisibilityFilter extends GroupAction {
    
    private Predicate m_filter;
    private Predicate m_predicate;
    
    /**
     * Create a new VisibilityFilter.
     * @param p the test predicate used to determine visibility
     */
    public VisibilityFilter(Predicate p) {
        setPredicate(p);
    }

    /**
     * Create a new VisibilityFilter.
     * @param group the data group to process
     * @param p the test predicate used to determine visibility
     */
    public VisibilityFilter(String group, Predicate p) {
        super(group);
        setPredicate(p);
    }

    /**
     * Create a new VisibilityFilter.
     * @param vis the Visualization to process
     * @param group the data group to process
     * @param p the test predicate used to determine visibility
     */
    public VisibilityFilter(Visualization vis, String group, Predicate p) {
        super(vis, group);
        setPredicate(p);
    }

    /**
     * Set the test predicate used to determine visibility.
     * @param p the test predicate to set
     */
    protected void setPredicate(Predicate p) {
        m_predicate = p;
        m_filter = new OrPredicate(p, VisiblePredicate.TRUE);
    }
    
    /**
     * @see prefux.action.Action#run(double)
     */
    public void run(double frac) {
        Iterator<VisualItem> items = m_vis.items(m_group, m_filter);
        while ( items.hasNext() ) {
            VisualItem item = (VisualItem)items.next();
            PrefuseLib.updateVisible(item, m_predicate.getBoolean(item));
        }
    }

} // end of class VisibilityAction
