/* HUYNH TOAN TRAVEL - app.js */
const initLenis=()=>{if(typeof Lenis==='undefined')return;const l=new Lenis({lerp:.08,smooth:true});function raf(t){l.raf(t);requestAnimationFrame(raf)}requestAnimationFrame(raf);window.__lenis=l};
const initHeader=()=>{const h=document.querySelector('.site-header'),p=document.querySelector('.scroll-progress');if(!h)return;window.addEventListener('scroll',()=>{const s=window.scrollY,mx=document.body.scrollHeight-window.innerHeight;h.classList.toggle('scrolled',s>60);if(p)p.style.width=(s/mx*100).toFixed(1)+'%'},{passive:true})};
const initHeroSwiper=()=>{if(typeof Swiper==='undefined')return;return new Swiper('.hero-swiper',{loop:true,speed:900,autoplay:{delay:5500,disableOnInteraction:false,pauseOnMouseEnter:true},effect:'fade',fadeEffect:{crossFade:true},navigation:{nextEl:'.hero-nav-next',prevEl:'.hero-nav-prev'},pagination:{el:'.hero-pg',clickable:true}})};
const initHeroParallax=()=>{const hero=document.querySelector('.hero-shell');if(!hero)return;hero.addEventListener('mousemove',e=>{const r=hero.getBoundingClientRect(),cx=(e.clientX-r.left)/r.width-.5,cy=(e.clientY-r.top)/r.height-.5;document.querySelectorAll('[data-parallax-speed]').forEach(el=>{const sp=parseFloat(el.dataset.parallaxSpeed)||1;el.style.transform=`translate(${cx*sp*30}px,${cy*sp*20}px)`})});hero.addEventListener('mouseleave',()=>{document.querySelectorAll('[data-parallax-speed]').forEach(el=>{el.style.transform='translate(0,0)';el.style.transition='transform 1.2s var(--ease-expo)'})})};
const initQuickSearch=()=>{document.querySelectorAll('.qs-tab').forEach(tab=>{tab.addEventListener('click',()=>{document.querySelectorAll('.qs-tab').forEach(t=>t.classList.remove('active'));document.querySelectorAll('.qs-panel').forEach(p=>p.classList.remove('active'));tab.classList.add('active');const t=document.querySelector(`.qs-panel[data-panel="${tab.dataset.target}"]`);if(t)t.classList.add('active')})})};
const initAOS=()=>{if(typeof AOS==='undefined')return;AOS.init({duration:700,easing:'ease-out-quart',once:true,offset:60})};
const initCounters=()=>{const cs=document.querySelectorAll('[data-counter]');if(!cs.length)return;const countUp=el=>{const tgt=parseFloat(el.dataset.counter),suf=el.dataset.suffix||'',dur=1800,start=performance.now();const upd=now=>{const prog=Math.min((now-start)/dur,1),ease=1-Math.pow(1-prog,4),val=tgt*ease;el.textContent=(Number.isInteger(tgt)?Math.floor(val):val.toFixed(1))+suf;if(prog<1)requestAnimationFrame(upd)};requestAnimationFrame(upd)};const obs=new IntersectionObserver(entries=>{entries.forEach(e=>{if(e.isIntersecting){countUp(e.target);obs.unobserve(e.target)}})},{threshold:.6});cs.forEach(c=>obs.observe(c))};
const initTilt=()=>{if(typeof VanillaTilt==='undefined')return;VanillaTilt.init(document.querySelectorAll('[data-tilt]'),{max:8,speed:400,glare:true,'max-glare':.15,perspective:1000})};
const initButtons=()=>{document.querySelectorAll('.btn-cta').forEach(btn=>{btn.addEventListener('click',e=>{const r=btn.getBoundingClientRect(),sz=Math.max(r.width,r.height),rip=document.createElement('span');rip.className='ripple-el';rip.style.cssText=`width:${sz}px;height:${sz}px;left:${e.clientX-r.left-sz/2}px;top:${e.clientY-r.top-sz/2}px`;btn.appendChild(rip);setTimeout(()=>rip.remove(),700)});btn.addEventListener('mousemove',e=>{const r=btn.getBoundingClientRect(),dx=(e.clientX-r.left-r.width/2)*.18,dy=(e.clientY-r.top-r.height/2)*.18;btn.style.transform=`translate(${dx}px,${dy}px)scale(1.03)`});btn.addEventListener('mouseleave',()=>{btn.style.transform='';btn.style.transition='transform .5s var(--ease-expo)'})})};
const initMobileNav=()=>{const toggle=document.querySelector('.mobile-nav-toggle'),nav=document.querySelector('.mobile-nav'),backdrop=document.querySelector('.mobile-nav-backdrop');if(!toggle||!nav)return;toggle.addEventListener('click',()=>nav.classList.toggle('open'));backdrop?.addEventListener('click',()=>nav.classList.remove('open'))};
const initStagger=()=>{document.querySelectorAll('[data-stagger-group]').forEach(group=>{const items=group.querySelectorAll('[data-stagger-item]');const obs=new IntersectionObserver(entries=>{if(entries[0].isIntersecting){items.forEach((item,i)=>{item.style.transitionDelay=`${i*.1}s`;item.classList.add('stagger-visible')});obs.disconnect()}},{threshold:.15});obs.observe(group)})};
const initLuxuryDropdowns=()=>{
    const selects=document.querySelectorAll('.qs-select, select.luxury-select');
    if(!selects.length)return;
    selects.forEach(sel=>{
        if(sel.dataset.customized==='true')return;
        sel.dataset.customized='true';
        sel.style.display='none';

        const wrap=document.createElement('div');
        wrap.className='custom-select-wrap';
        if(sel.classList.contains('input-glass')) {
            wrap.classList.add('in-input-glass');
        }

        // Preserve left prefix icons, hide right arrow spans
        const parent = sel.parentElement;
        if(parent) {
            let hasPrefix = false;
            parent.querySelectorAll('span').forEach(sp => {
                if(sp.textContent.trim() === '▼' || sp.classList.contains('icon-arrow') || sp.classList.contains('right-3.5')) {
                    sp.style.display = 'none';
                } else if (sp.classList.contains('icon-prefix') || sp.classList.contains('left-3.5') || sel.classList.contains('pl-11')) {
                    hasPrefix = true;
                    sp.style.zIndex = '5';
                    sp.style.pointerEvents = 'none';
                }
            });
            if(hasPrefix || sel.classList.contains('pl-11')) {
                wrap.classList.add('has-prefix-icon');
            }
        }

        const trigger=document.createElement('button');
        trigger.type='button';
        trigger.className='custom-select-trigger font-bold';
        if(sel.classList.contains('input-glass')) {
            trigger.className += ' input-glass rounded-xl';
        }

        const textSpan=document.createElement('span');
        textSpan.className='trigger-text';
        textSpan.textContent=sel.options[sel.selectedIndex]?.textContent||sel.options[0]?.textContent||'';

        const arrowSvg=document.createElementNS('http://www.w3.org/2000/svg','svg');
        arrowSvg.setAttribute('class','arrow-icon');
        arrowSvg.setAttribute('fill','none');
        arrowSvg.setAttribute('stroke','currentColor');
        arrowSvg.setAttribute('viewBox','0 0 24 24');
        arrowSvg.innerHTML='<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M19 9l-7 7-7-7"/>';

        trigger.appendChild(textSpan);
        trigger.appendChild(arrowSvg);

        const menu=document.createElement('div');
        menu.className='custom-select-menu';

        const renderItems=()=>{
            menu.innerHTML='';
            Array.from(sel.options).forEach(opt=>{
                const item=document.createElement('div');
                item.className='custom-select-item'+(opt.value===sel.value?' active':'');
                item.dataset.value=opt.value;
                const label=document.createElement('span');
                label.className='select-item-label';
                label.textContent=opt.textContent;
                const check=document.createElement('span');
                check.className='item-check';
                check.textContent='✓';
                item.appendChild(label);
                item.appendChild(check);
                item.addEventListener('click',e=>{
                    e.stopPropagation();
                    sel.value=opt.value;
                    textSpan.textContent=opt.textContent;
                    menu.querySelectorAll('.custom-select-item').forEach(i=>i.classList.remove('active'));
                    item.classList.add('active');
                    wrap.classList.remove('open');
                    sel.dispatchEvent(new Event('change',{bubbles:true}));
                    sel.dispatchEvent(new Event('input',{bubbles:true}));
                });
                menu.appendChild(item);
            });
        };
        renderItems();

        trigger.addEventListener('click',e=>{
            e.stopPropagation();
            const isOpen=wrap.classList.contains('open');
            document.querySelectorAll('.custom-select-wrap.open').forEach(w=>w.classList.remove('open'));
            if(!isOpen){
                renderItems();
                const rect = wrap.getBoundingClientRect();
                if (window.innerWidth - rect.left < 330) {
                    menu.classList.add('align-right');
                } else {
                    menu.classList.remove('align-right');
                }
                wrap.classList.add('open');
                const activeItem = menu.querySelector('.custom-select-item.active');
                if(activeItem) {
                    activeItem.scrollIntoView({ block: 'nearest' });
                }
            }
        });

        sel.addEventListener('change',()=>{
            textSpan.textContent=sel.options[sel.selectedIndex]?.textContent||'';
            menu.querySelectorAll('.custom-select-item').forEach(i=>{
                i.classList.toggle('active',i.dataset.value===sel.value);
            });
        });

        wrap.appendChild(trigger);
        wrap.appendChild(menu);
        sel.parentNode.insertBefore(wrap,sel.nextSibling);
    });

    document.addEventListener('click',e=>{
        if(!e.target.closest('.custom-select-wrap')){
            document.querySelectorAll('.custom-select-wrap.open').forEach(w=>w.classList.remove('open'));
        }
    });

    document.addEventListener('keydown',e=>{
        if(e.key==='Escape'){
            document.querySelectorAll('.custom-select-wrap.open').forEach(w=>w.classList.remove('open'));
        }
    });

    document.querySelectorAll('.qs-tab').forEach(t=>{
        t.addEventListener('click',()=>{
            document.querySelectorAll('.custom-select-wrap.open').forEach(w=>w.classList.remove('open'));
        });
    });
};
window.initLuxuryDropdowns=initLuxuryDropdowns;
document.addEventListener('DOMContentLoaded',()=>{initLenis();initHeader();initHeroSwiper();initHeroParallax();initQuickSearch();initLuxuryDropdowns();initAOS();initCounters();initTilt();initButtons();initMobileNav();initStagger()});

