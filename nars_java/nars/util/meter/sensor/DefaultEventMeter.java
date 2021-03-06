/* Copyright 2009 - 2010 The Stajistics Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nars.util.meter.sensor;

import nars.util.meter.session.StatsSession;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultEventMeter extends AbstractSensor implements EventMeter {

    //private static final Logger logger = Logger.getLogger(DefaultEventMeter.class.toString());

    public DefaultEventMeter(final StatsSession session) {
        super(session);
    }

    public DefaultEventMeter(final String id) {
        super(id);
    }

    @Override
    public EventMeter event() {
        //try {
        
        value = 1;

        final long now = System.currentTimeMillis();

        session.track(this, now);
        session.update(this, now);
            
        /*} catch (Exception e) {
            Misc.logHandledException(logger, e, "Caught Exception in incident()");
            Misc.handleUncaughtException(getKey(), e);
        }*/

        return this;
    }

//    public static class Factory implements TrackerFactory<IncidentTracker> {
//
//        @Override
//        public IncidentTracker createTracker(final StatsKey key,
//                                             final StatsSessionManager sessionManager) {
//            return new DefaultIncidentTracker(sessionManager.getOrCreateSession(key));
//        }
//        
//        @Override
//        public Class<IncidentTracker> getTrackerType() {
//            return IncidentTracker.class;
//        }
//    }
}
