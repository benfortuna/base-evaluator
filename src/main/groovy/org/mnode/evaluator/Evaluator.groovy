/**

* This file is part of Base Modules.
 *
 * Copyright (c) 2010, Ben Fortuna [fortuna@micronode.com]
 *
 * Base Modules is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Base Modules is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Base Modules.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mnode.evaluator

import groovy.swing.SwingBuilder
import groovy.lang.GroovyShell
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.PopupMenu
import java.awt.MenuItem
import javax.swing.JFrame
import javax.swing.JComponent
import javax.swing.KeyStroke
import javax.swing.Action
import java.awt.event.MouseEvent
import javax.swing.DefaultListCellRendererimport java.awt.Componentimport javax.swing.JListimport javax.swing.DefaultListModel
import groovy.swing.LookAndFeelHelper
import java.awt.BorderLayout

//@Grapes([
//    @Grab(group='com.seaglasslookandfeel', module='seaglasslookandfeel', version='0.1.7.2')])
class Evaluator {

     static void close(def frame, def exit) {
         if (exit) {
             System.exit(0)
         }
         else {
             frame.visible = false
         }
     }

  static void main(def args) {
    LookAndFeelHelper.instance.addLookAndFeelAlias('seaglass', 'com.seaglasslookandfeel.SeaGlassLookAndFeel')

    def shell = new GroovyShell()
    
    def evaluate = { expression ->
        shell.evaluate(expression)
    }
    
    def swing = new SwingBuilder()
    swing.edt {
      lookAndFeel('seaglass') //, 'substance', 'system')

      frame(title: 'Evaluator', size: [350, 480], show: true, locationRelativeTo: null,
              defaultCloseOperation: JFrame.DO_NOTHING_ON_CLOSE, iconImage: imageIcon('/logo-16.png', id: 'logo').image, id: 'evaluatorFrame') {
          
          actions {
              action(id: 'evaluate')
          }
          
          menuBar {
              menu(text: 'View', mnemonic: 'V') {
                  checkBoxMenuItem(text: "Number Pad", id: 'viewNumPad')
              }
          }
          
          borderLayout()
//          tabbedPane(border: emptyBorder(5), id: 'tabs') {
              panel(name: 'Sheet 1') {
                  borderLayout()
                  scrollPane() {
//                      textArea(editable: false, columns: 15, rows: 4, id: 'resultField')
                        list(id: 'evaluations')
                        evaluations.cellRenderer = new EvaluationListCellRenderer()
                        def evaluationModel = new DefaultListModel()
                        evaluations.model = evaluationModel
                  }
                  textField(constraints: BorderLayout.SOUTH, columns: 15, id: 'inputField')
                  inputField.actionPerformed = {
                      if (inputField.text) {
//                          resultField.text = "${resultField.text}\n${evaluate(inputField.text)}"
                            def evaluation = new Evaluation()
                            evaluation.input = inputField.text
                            evaluation.result = evaluate(inputField.text)
                            evaluations.model.addElement(evaluation)
                          inputField.text = ""
                      }
                  }
              }
//          }
          panel(constraints: BorderLayout.SOUTH, border: emptyBorder(5), id: 'numPad') {
              gridLayout(rows: 4, columns: 3)
              button(text: '7', id: 'but7')
              but7.actionPerformed = { inputField.text = "${inputField.text}7" }
              //but7.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('7'), 'doClick')
              //but7.actionMap.put('doClick', { but7.doClick() } as Action)
              
              button(text: '8', id: 'but8')
              but8.actionPerformed = { inputField.text = "${inputField.text}8" }
              button(text: '9', id: 'but9')
              but9.actionPerformed = { inputField.text = "${inputField.text}9" }
              button(text: '/')
              
              button(text: '4', id: 'but4')
              but4.actionPerformed = { inputField.text = "${inputField.text}4" }
              button(text: '5', id: 'but5')
              but5.actionPerformed = { inputField.text = "${inputField.text}5" }
              button(text: '6', id: 'but6')
              but6.actionPerformed = { inputField.text = "${inputField.text}6" }
              button(text: '*')
              
              button(text: '1', id: 'but1')
              but1.actionPerformed = { inputField.text = "${inputField.text}1" }
              button(text: '2', id: 'but2')
              but2.actionPerformed = { inputField.text = "${inputField.text}2" }
              button(text: '3', id: 'but3')
              but3.actionPerformed = { inputField.text = "${inputField.text}3" }
              button(text: '-')
              
              button(text: '0', id: 'but0')
              but0.actionPerformed = { inputField.text = "${inputField.text}0" }
              button(text: '.')
              button(text: '+')
              button(text: '=')
          }
          bind(source: viewNumPad, sourceProperty:'selected', target: numPad, targetProperty:'visible')
          
          if (SystemTray.isSupported()) {
              TrayIcon trayIcon = new TrayIcon(logo.image, 'Evaluator')
              trayIcon.imageAutoSize = false
              trayIcon.mousePressed = { event ->
                  if (event.button == MouseEvent.BUTTON1) {
                      evaluatorFrame.visible = true
                  }
              }
              
              PopupMenu popupMenu = new PopupMenu('Evaluator')
              MenuItem openMenuItem = new MenuItem('Open')
              openMenuItem.actionPerformed = {
                  evaluatorFrame.visible = true
              }
              popupMenu.add(openMenuItem)
              popupMenu.addSeparator()
              MenuItem exitMenuItem = new MenuItem('Exit')
              exitMenuItem.actionPerformed = {
                  close(evaluatorFrame, true)
              }
              popupMenu.add(exitMenuItem)
              trayIcon.popupMenu = popupMenu
              
              SystemTray.systemTray.add(trayIcon)
          }
      }
      evaluatorFrame.windowClosing = {
          close(evaluatorFrame, !SystemTray.isSupported())
      }
    }
  }
}

class Evaluation {
    def input
    def result
    
    String toString() {
        return "<html><p>${input}</p><p> = ${result}</p></html>"
    }
}

class EvaluationListCellRenderer extends DefaultListCellRenderer {
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        return this
    }
}